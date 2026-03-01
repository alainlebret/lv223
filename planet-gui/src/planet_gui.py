# LV-223 (Colonization) multi-agent simulation
#
# Copyright (c) 2019–2026 Alain Lebret (ENSICAEN)
#
# SPDX-License-Identifier: MIT

"""
planet_gui.py

This file contains the PlanetGUI class, which is responsible for creating and 
managing the graphical user interface (GUI) for the planet conquest simulation. 
It is based on a JavaFX GUI created for the same simulation by Antoine
Lucerna-Grives (unfortunatly without the blurred effect and the smooth transitions).

It uses Tkinter for creating the GUI, and it uses sockets for communicating 
with the planet server. 

Copyright (C) (c) 2019–2026 Alain Lebret (alain.lebret@ensicaen.fr)

This file is part of lv223, distributed under the MIT license.
"""

import tkinter as tk
from tkinter import ttk, messagebox
from tkinter.font import Font
from typing import Dict, Tuple
import socket
import threading
import queue
import json
import logging
import time

class PlanetGUI:
    RIGHT_PANEL_STYLE  = "RightPanel.TFrame"
    LEGEND_TITLE_STYLE = "LegendTitle.TLabel"
    LEGEND_VALUE_STYLE = "LegendValue.TLabel"
    LABEL_TITLE_STYLE  = "LabelTitle.TLabel"
    LABEL_VALUE_STYLE  = "LabelValue.TLabel"
    BUTTON_STYLE       = "GUIButton.TButton"
    PANEL_BG_COLOR     = 'gray19'

    # Original colors
    ORIGINAL_COLOR_MAPPINGS = {
        "WATER": "darkturquoise",
        "STONE": "dimgray",
        "DRY_PRAIRIE": "greenyellow",
        "PRAIRIE": "yellowgreen",
        "WET_PRAIRIE": "limegreen",
        "DESERT": "palegoldenrod",
        "MINERAL": "silver",
        "FRUITS_AND_VEGETABLES": "tomato",
        "IMPENETRABLE": "black",
        "FOREST": "forestgreen",
        "BASE": "mediumorchid"
    }

    # Pastels
    PASTEL_COLOR_MAPPINGS = {
        "WATER": "lightblue",
        "STONE": "gainsboro",
        "DRY_PRAIRIE": "palegreen",
        "PRAIRIE": "lightgreen",
        "WET_PRAIRIE": "mediumaquamarine",
        "DESERT": "khaki",
        "MINERAL": "silver",
        "FRUITS_AND_VEGETABLES": "coral",
        "IMPENETRABLE": "gray",
        "FOREST": "mediumseagreen",
        "BASE": "plum"
    }

    # Reality
    COLOR_MAPPINGS = {
        "WATER": "lightskyblue",
        "STONE": "slategray",
        "DRY_PRAIRIE": "khaki",
        "PRAIRIE": "lightgreen",
        "WET_PRAIRIE": "mediumseagreen",
        "DESERT": "cornsilk",
        "MINERAL": "silver",
        "FRUITS_AND_VEGETABLES": "darksalmon",
        "IMPENETRABLE": "black",
        "FOREST": "olivedrab",
        "BASE": "mediumorchid"
    }

    ROBOT_COLOR_MAPPINGS = {
        "Cartographer": "dodgerblue",
        "Farmer": "darkorchid",
        "Harvester": "darkorange",
        "Miner": "deeppink",
        "Pipeliner": "darkseagreen"
    }

    # Configure logging (ensure this is called once at program startup)
    logging.basicConfig(level=logging.WARN, format='%(asctime)s - %(levelname)s - %(message)s')

    def __init__(self, master, width: int, height: int, cell_width: int = 40, cell_height: int = 40,
                 host: str = "localhost", port: int = 12345):
        """
        Initializes the PlanetGUI with the given parameters.

        Parameters:
            master (tk.Tk): The main Tkinter root.
            width (int): The width of the grid (number of cells horizontally).
            height (int): The height of the grid (number of cells vertically).
            cell_width (int, optional): The pixel width of each cell. Defaults to 40.
            cell_height (int, optional): The pixel height of each cell. Defaults to 40.
            host (str, optional): The server host to connect to. Defaults to "localhost".
            port (int, optional): The server port to connect to. Defaults to 12345.
        """
        self.logger = logging.getLogger(__name__)
        self.host = host
        self.port = port
        self.robot_positions: Dict[str, Tuple[int, int]] = {}
        self.setup_attributes(master, width, height, cell_width, cell_height)
        self.setup_gui_components()
        self.update_queue = queue.Queue()  # Thread-safe queue for incoming messages

    def setup_attributes(self, master, width: int, height: int, cell_width: int, cell_height: int):
        """
        Sets up instance attributes.

        Parameters:
            master (tk.Tk): The root Tkinter object.
            width (int): The width of the grid (number of columns).
            height (int): The height of the grid (number of rows).
            cell_width (int): Width of each cell in pixels.
            cell_height (int): Height of each cell in pixels.
        """
        self.running = True
        self.client_socket = None
        self.master = master
        self.width = width
        self.height = height
        self.cell_width = cell_width
        self.cell_height = cell_height
        # Initialize grid as a 2D list with dimensions height x width
        self.grid = [[None for _ in range(width)] for _ in range(height)]
        self.health = "Good"
        self.season = "Summer"
        self.turns = 0
        self.client_socket_connected = False
        self.font_legend_title = Font(family="Arial", size=13, weight='bold', underline=True)
        self.font_legend_value = Font(family="Arial", size=12)
        self.font_label_title = Font(family="Arial", size=12, weight='bold')
        self.font_label_value = Font(family="Arial", size=12)
        self.style = ttk.Style()

    def setup_gui_components(self):
        """
        Sets up GUI components including the grid, right panel, and window protocols.
        """
        self.setup_grid()
        self.setup_right_panel()
        self.master.protocol("WM_DELETE_WINDOW", self.on_close)

    def setup_grid(self):
        """
        Creates the grid frame and populates it with canvas widgets for each cell.
        """
        grid_frame = tk.Frame(self.master)
        grid_frame.pack(side=tk.LEFT, fill=tk.BOTH, expand=True, padx=10)
        for y in range(self.height):
            for x in range(self.width):
                canvas = self.create_cell_canvas(grid_frame, x, y)
                self.grid[y][x] = canvas

    def create_cell_canvas(self, parent, x: int, y: int) -> tk.Canvas:
        """
        Creates a canvas widget for a grid cell.

        Parameters:
            parent (tk.Widget): The parent widget.
            x (int): X coordinate in the grid.
            y (int): Y coordinate in the grid.

        Returns:
            tk.Canvas: The created canvas.
        """
        canvas = tk.Canvas(parent, width=self.cell_width, height=self.cell_height,
                           borderwidth=0, highlightthickness=0)
        canvas.grid(row=y, column=x)
        canvas.create_rectangle(0, 0, self.cell_width, self.cell_height, fill="floralwhite",
                                outline='white', tags="rect")
        return canvas

    def setup_right_panel(self):
        """
        Configures the right panel of the GUI.
        """
        self.configure_right_panel_style()
        right_panel_frame = self.create_right_panel_frame()
        self.add_terrain_section(right_panel_frame)
        ttk.Label(right_panel_frame, text="", style=PlanetGUI.RIGHT_PANEL_STYLE).pack(pady=10)
        self.add_robot_section(right_panel_frame)
        ttk.Label(right_panel_frame, text="", style=PlanetGUI.RIGHT_PANEL_STYLE).pack(pady=10)
        self.add_info_sections(right_panel_frame)
        ttk.Label(right_panel_frame, text="", style=PlanetGUI.RIGHT_PANEL_STYLE).pack(pady=10)
        self.add_control_buttons(right_panel_frame)

    def configure_right_panel_style(self):
        """
        Configures the style settings for the right panel.
        """
        self.style.configure(PlanetGUI.RIGHT_PANEL_STYLE, background=PlanetGUI.PANEL_BG_COLOR)
        self.style.configure(PlanetGUI.LEGEND_TITLE_STYLE, background=PlanetGUI.PANEL_BG_COLOR,
                             foreground='white', font=self.font_legend_title)
        self.style.configure(PlanetGUI.LEGEND_VALUE_STYLE, background=PlanetGUI.PANEL_BG_COLOR,
                             foreground='white', font=self.font_legend_value)
        self.style.configure(PlanetGUI.LABEL_TITLE_STYLE, background=PlanetGUI.PANEL_BG_COLOR,
                             foreground='white', font=self.font_label_title)
        self.style.configure(PlanetGUI.LABEL_VALUE_STYLE, background='black',
                             foreground='lightyellow', font=self.font_label_value)
        self.style.map(PlanetGUI.BUTTON_STYLE,
                       foreground=[('active','darkorange'), ('!disabled', 'gold')],
                       background=[('active','gray'), ('!disabled', 'dimgray')])

    def create_right_panel_frame(self) -> ttk.Frame:
        """
        Creates the frame for the right panel.

        Returns:
            ttk.Frame: The right panel frame.
        """
        right_panel_frame = ttk.Frame(self.master, style=self.RIGHT_PANEL_STYLE)
        right_panel_frame.pack(side=tk.RIGHT, fill=tk.Y, padx=10)
        return right_panel_frame

    def add_terrain_section(self, parent: ttk.Frame):
        """
        Adds a terrain legend section to the right panel.
        """
        ttk.Label(parent, text="Terrains", style=PlanetGUI.LEGEND_TITLE_STYLE).pack(pady=(0, 10))
        for terrain_type, color in PlanetGUI.COLOR_MAPPINGS.items():
            self.create_color_caption(parent, self.format_cell_type(terrain_type), color)

    def add_robot_section(self, parent: ttk.Frame):
        """
        Adds a robot legend section to the right panel.
        """
        ttk.Label(parent, text="Robots", style=PlanetGUI.LEGEND_TITLE_STYLE).pack(pady=(5, 10))
        for robot_type, color in PlanetGUI.ROBOT_COLOR_MAPPINGS.items():
            self.create_color_caption(parent, robot_type, color, is_robot=True)

    def add_info_sections(self, parent: ttk.Frame):
        """
        Adds information sections (health, season, turns) to the right panel.
        """
        self.health_value_label = self.create_info_section(parent, "Health", self.health, "health_value_label")
        self.season_value_label = self.create_info_section(parent, "Season", self.season, "season_value_label")
        self.turns_value_label = self.create_info_section(parent, "Turns", str(self.turns), "turns_value_label")

    def create_info_section(self, parent: ttk.Frame, title: str, initial_value: str, label_name: str) -> ttk.Label:
        """
        Creates an information section in the right panel.

        Parameters:
            parent (ttk.Frame): Parent frame.
            title (str): Section title.
            initial_value (str): Initial displayed value.
            label_name (str): Unique name for the value label.

        Returns:
            ttk.Label: The created value label.
        """
        frame = ttk.Frame(parent, style=PlanetGUI.RIGHT_PANEL_STYLE)
        frame.pack(fill=tk.X, pady=5)
        frame.grid_columnconfigure(1, weight=1)
        ttk.Label(frame, text=f"{title}:", style=PlanetGUI.LABEL_TITLE_STYLE).grid(row=0, column=0, sticky='w')
        value_label = ttk.Label(frame, text=initial_value, style=PlanetGUI.LABEL_VALUE_STYLE, name=label_name)
        value_label.grid(row=0, column=1, sticky='w', padx=10)
        return value_label

    def add_control_buttons(self, parent: ttk.Frame):
        """
        Adds control buttons Connect/Quit to the right panel.
        """
        self.connect_button = ttk.Button(parent, text="Connect", style=PlanetGUI.BUTTON_STYLE,
                                         command=self.handle_connect_click)
        self.connect_button.pack(pady=(5, 5))

    def create_color_caption(self, parent: ttk.Frame, label_text: str, bg_color: str, is_robot: bool = False):
        """
        Creates a caption with a colored shape for legends.

        Parameters:
            parent (ttk.Frame): Parent frame.
            label_text (str): Text for the caption.
            bg_color (str): Background color for the shape.
            is_robot (bool, optional): If True, draw an oval; otherwise, a rectangle.
        """
        caption_frame = ttk.Frame(parent, style=PlanetGUI.RIGHT_PANEL_STYLE)
        caption_frame.pack(fill=tk.X, pady=2)
        shape_canvas = tk.Canvas(caption_frame, width=25, height=25, bg=PlanetGUI.PANEL_BG_COLOR,
                                  highlightthickness=0)
        shape_canvas.pack(side=tk.LEFT, padx=(10, 5))
        if is_robot:
            radius = 8
            shape_canvas.create_oval(5, 5, 5 + radius * 2, 5 + radius * 2, fill=bg_color, outline="")
        else:
            shape_canvas.create_rectangle(5, 5, 25, 25, fill=bg_color, outline='')
        ttk.Label(caption_frame, text=f"{label_text}", style=PlanetGUI.LEGEND_VALUE_STYLE).pack(side=tk.LEFT, fill=tk.X)

    def show_popup(self, message: str):
        """
        Displays a popup message.

        Parameters:
            message (str): Message to display.
        """
        messagebox.showinfo("Simulation terminated", message)

    def show_about_box(self, event=None):
        about_text = "LV223 Simulation\nVersion 1.0\n© 2021-2024 Alain Lebret"
        messagebox.showinfo("About Planet Simulation", about_text)

    def setup_key_bindings(self):
        """
        Sets up key bindings for shortcuts.
        """
        self.master.bind('<h>', self.show_about_box)
        self.master.bind('<H>', self.show_about_box)
        self.master.bind('<Escape>', self.disconnect_and_close)
        self.master.bind('<q>', self.disconnect_and_close)
        self.master.bind('<Q>', self.disconnect_and_close)

    def handle_connect_click(self):
        """
        Handles the Connect/Quit button click.
        """
        if self.connect_button['text'] == "Connect":
            self.connect_to_planet_server(self.host, self.port)
            self.connect_button.config(text="Quit")
            # Optionally, signal readiness via a temporary file
            try:
                with open(".tmp/colony_client_ready", 'w') as f:
                    f.write("ready")
            except Exception as e:
                self.logger.error("Error writing temporary readiness file: %s", e)
        else:
            self.disconnect_and_close()

    def disconnect_and_close(self, event=None):
        """
        Disconnects from the server and closes the GUI.
        """
        if self.client_socket and not self.client_socket._closed:
            try:
                self.client_socket.sendall(b'{"action": "quit"}\n')
            except Exception as e:
                self.logger.error("Error sending quit command: %s", e)
        self.running = False
        if self.client_socket:
            try:
                self.client_socket.close()
            except Exception as e:
                self.logger.error("Error closing socket: %s", e)
        self.master.destroy()

    def format_cell_type(self, cell_type: str) -> str:
        """
        Converts a cell type string from uppercase to title case.

        Parameters:
            cell_type (str): The cell type string.

        Returns:
            str: The formatted string.
        """
        return cell_type.replace("_", " ").title()

    def on_close(self):
        self.disconnect_and_close()

    def connect_to_planet_server(self, host: str, port: int):
        """
        Connects to the planet server.

        Parameters:
            host (str): Server hostname or IP.
            port (int): Server port.
        """
        if self.client_socket and not self.client_socket._closed:
            self.logger.info("Already connected. Please disconnect first.")
            return
        try:
            self.client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            self.client_socket.connect((host, port))
            self.client_socket.sendall(b"GUI_CLIENT\n")
            self.start_listening()
        except Exception as e:
            self.logger.error("Error connecting to server at %s:%d - %s", host, port, e)

    def start_listening(self):
        """
        Starts a background thread to receive server messages and schedules
        GUI updates on the main thread via after().
        """
        threading.Thread(target=self.listen_to_planet_server, daemon=True).start()
        self.master.after(16, self.poll_updates)

    def listen_to_planet_server(self):
        """
        Listens to incoming messages from the server.
        """
        while self.running:
            try:
                message_length = self.client_socket.recv(4)
                if message_length:
                    length = int.from_bytes(message_length, 'big')
                    message = b''
                    while len(message) < length:
                        part = self.client_socket.recv(length - len(message))
                        if not part:
                            raise ConnectionError("Socket connection broken")
                        message += part
                    full_message = message.decode('utf-8')
                    self.logger.debug("Received data: %s", full_message)
                    self.update_queue.put(full_message)
                else:
                    time.sleep(0.1)
            except OSError as e:
                if self.running:
                    self.logger.error("Error receiving message: %s", e)
                break

    def poll_updates(self):
        """
        Drains the incoming message queue and processes each message on the
        main thread. Reschedules itself via after() while the GUI is running.
        """
        try:
            while True:
                message = self.update_queue.get_nowait()
                self.process_message(message)
        except queue.Empty:
            pass
        if self.running:
            self.master.after(16, self.poll_updates)

    def process_message(self, message: str):
        """
        Processes a server message.

        Parameters:
            message (str): The received JSON message.
        """
        try:
            data = json.loads(message)
            self.update_health(data.get('health', self.health))
            self.update_season(data.get('season', self.season).capitalize())
            self.update_turns(data.get('turns', self.turns))
            for cell_update in data.get('terrain', []):
                self.update_cell(
                    cell_update['x'],
                    cell_update['y'],
                    cell_update['type'],
                    cell_update['quantity'],
                    cell_update['visited'],
                    cell_update['modified'],
                    cell_update['has_pipeline']
                )
            self.clear_robot_positions()
            for robot_info in data.get('robots', []):
                self.update_robot(robot_info)
            self.master.update_idletasks()
            if data.get('end_simulation', False):
                self.show_popup(f"The simulation has ended. Total turns: {self.turns}")
                self.running = False
                self.send_acknowledgment()
                return
            self.send_acknowledgment()
        except Exception as e:
            self.logger.error("Error processing message: %s", e)

    def update_cell(self, x, y, cell_type, quantity, visited, modified, has_pipeline):
        """
        Updates a cell's appearance based on provided state.

        Parameters:
            x (int): Cell x-coordinate.
            y (int): Cell y-coordinate.
            cell_type (str): Cell type.
            quantity (int): Resource quantity.
            visited: Boolean or string ("true"/"false").
            modified: Boolean or string ("true"/"false").
            has_pipeline: Boolean or string ("true"/"false").
        """
        try:
            x, y = int(x), int(y)
        except ValueError:
            self.logger.error("Invalid cell coordinates: %s, %s", x, y)
            return
        
        # Convert values to booleans if needed
        visited_bool = str(visited).lower() == "true"
        modified_bool = str(modified).lower() == "true"
        has_pipeline_bool = str(has_pipeline).lower() == "true"

        canvas = self.grid[y][x]
        base_color = PlanetGUI.COLOR_MAPPINGS.get(cell_type, "floralwhite")
        is_hidden = (modified_bool and visited_bool) or (not visited_bool)
        color = "whitesmoke" if is_hidden else base_color

        current_color = canvas.itemcget("rect", "fill")
        if current_color != color:
            canvas.itemconfig("rect", fill=color)

        if is_hidden:
            # Ensure hidden cells do not keep stale overlays from previous visible states.
            canvas.delete("pipeline")
            canvas.delete(f"resource_text_{x}_{y}")
            return

        if has_pipeline_bool and visited_bool:
            self.draw_smaller_dashed_cross(canvas)
        if cell_type in ["WATER", "MINERAL", "FRUITS_AND_VEGETABLES"] or \
           ((cell_type in ["PRAIRIE", "WET_PRAIRIE", "DRY_PRAIRIE"]) and quantity > 100):
            self.update_resource_text(x, y, quantity)

    def draw_pipeline_segment(self, canvas):
        """
        Draws a dashed line representing a pipeline segment.
        """
        canvas.create_line(0, self.cell_height // 2, self.cell_width, self.cell_height // 2,
                           dash=(4, 2), fill='aliceblue', tags='pipeline')

    def draw_dashed_cross(self, canvas):
        """
        Draws a dashed cross on the canvas.
        """
        x1, y1, x2, y2 = 0, 0, self.cell_width, self.cell_height
        canvas.create_line(x1, y1, x2, y2, dash=(4, 2), fill='aliceblue', tags='pipeline')
        canvas.create_line(x1, y2, x2, y1, dash=(4, 2), fill='aliceblue', tags='pipeline')

    def draw_smaller_dashed_cross(self, canvas):
        """
        Draws a smaller dashed cross on the canvas.
        """
        center_x, center_y = self.cell_width / 2, self.cell_height / 2
        offset_x, offset_y = self.cell_width / 6, self.cell_height / 6
        x1, y1 = center_x - offset_x, center_y - offset_y
        x2, y2 = center_x + offset_x, center_y + offset_y
        canvas.create_line(x1, y1, x2, y2, dash=(1, 1), fill='aliceblue', tags='pipeline')
        x1, y1 = center_x + offset_x, center_y - offset_y
        x2, y2 = center_x - offset_x, center_y + offset_y
        canvas.create_line(x1, y1, x2, y2, dash=(1, 1), fill='aliceblue', tags='pipeline')

    def update_resource_text(self, x, y, quantity):
        """
        Updates resource quantity text for a cell.
        """
        canvas = self.grid[y][x]
        text_tag = f"resource_text_{x}_{y}"
        existing_text = canvas.find_withtag(text_tag)
        text_x = self.cell_width / 2
        text_y = self.cell_height - 5
        percentage_text = f"{quantity} u"
        if existing_text:
            canvas.itemconfig(existing_text, text=percentage_text)
        else:
            canvas.create_text(text_x, text_y, text=percentage_text, tags=text_tag,
                               font=('Arial', 6), fill='white')

    def clear_robot_positions(self):
        """
        Clears all robot positions from the grid.
        """
        for (x, y) in self.robot_positions.values():
            canvas = self.grid[y][x]
            canvas.delete("robot")
        self.robot_positions.clear()

    def update_robot(self, robot_info: dict):
        """
        Updates a robot's position on the grid based on provided info.

        Parameters:
            robot_info (dict): Contains keys 'position', 'type', and 'id'.
        """
        try:
            x = int(robot_info['position']['x'])
            y = int(robot_info['position']['y'])
            robot_type = robot_info['type']
            robot_id = robot_info['id']
            color = PlanetGUI.ROBOT_COLOR_MAPPINGS.get(robot_type, "red")
            self.place_robot(x, y, color)
        except (ValueError, KeyError) as e:
            self.logger.error("Error processing robot info: %s, Error: %s", robot_info, e)

    def place_robot(self, x: int, y: int, color: str):
        """
        Places or updates a robot's representation on the grid.

        Parameters:
            x (int): x-coordinate.
            y (int): y-coordinate.
            color (str): Color for the robot.
        """
        radius = self.cell_width // 4
        center_x = self.cell_width // 2
        center_y = self.cell_height // 2
        canvas = self.grid[y][x]
        if not canvas.find_withtag("robot"):
            canvas.create_oval(center_x - radius, center_y - radius,
                               center_x + radius, center_y + radius,
                               fill=color, outline="", tags="robot")
        else:
            canvas.itemconfig("robot", fill=color, outline="")
        self.robot_positions[(x, y)] = (x, y)

    def smoothed_oval(self, canvas, x, y, width, height, color, resolution=64):
        """
        Draws a smoothed oval using a polygon.
        """
        points = [x, y, x+width, y, x+width, y+height, x, y+height, x, y]
        return canvas.create_polygon(points, fill=color, smooth=True, splinesteps=resolution, tags="robot")

    def poly_roundrect(self, canvas, x, y, width, height, radius, color, resolution=32):
        """
        Draws a rounded rectangle using a smoothed polygon.
        """
        radius = min(min(width, height), radius*2)
        points = [x, y,
                  x+radius, y,
                  x+(width-radius), y,
                  x+width, y,
                  x+width, y+radius,
                  x+width, y+(height-radius),
                  x+width, y+height,
                  x+(width-radius), y+height,
                  x+radius, y+height,
                  x, y+height,
                  x, y+(height-radius),
                  x, y+radius,
                  x, y]
        return canvas.create_polygon(points, fill=color, smooth=True, splinesteps=resolution, tags="robot")

    def update_health(self, health):
        """
        Updates the displayed health value.

        Parameters:
            health (str): The new health state.
        """
        self.health = health
        self.health_value_label.config(text=self.health)

    def update_season(self, season):
        """
        Updates the displayed season.

        Parameters:
            season (str): The new season.
        """
        self.season = season.capitalize()
        self.season_value_label.config(text=self.season)

    def update_turns(self, turns):
        """
        Updates the displayed turn count.

        Parameters:
            turns (int): The new turn number.
        """
        self.turns = turns
        self.turns_value_label.config(text=self.turns)

    def send_acknowledgment(self):
        """
        Sends an 'ACK' message to the server.
        """
        if self.client_socket and not self.client_socket._closed:
            try:
                self.client_socket.sendall(b'ACK\n')
            except OSError as e:
                self.logger.error("Error sending acknowledgment: %s", e)

def main():
    """
    Initializes and starts the planet simulation GUI.
    """
    root = tk.Tk()
    root.title("LV223 Simulation")
    root.resizable(False, False)
    root.configure(bg='gray19')
    app = PlanetGUI(root, width=21, height=21)
    app.setup_key_bindings()
    root.mainloop()

if __name__ == "__main__":
    main()
