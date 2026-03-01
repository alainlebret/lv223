# GitHub + GitLab Bootstrap (From Zero)

Use this when:
- local folder exists with your files
- no repository exists yet on GitHub/GitLab

## 1) Create empty remote repositories

- Create `USER/PROJECT` on GitHub (empty repo: no README, no .gitignore, no license).
- Create `USER/PROJECT` on GitLab (empty repo too).

## 2) Initialize local repo

```bash
cd /path/to/PROJECT
git init
git branch -M main
git add .
git commit -m "Initial commit"
```

## 3) Configure remotes

Recommended:
- GitHub with SSH
- GitLab with HTTPS (simpler auth in your context)

```bash
git remote add github git@github.com:USER/PROJECT.git
git remote add gitlab https://gitlab.ecole.ensicaen.fr/USER/PROJECT.git
git remote -v
```

## 4) Push initial branch

```bash
git push -u github main
git push -u gitlab main
```

If GitLab rejects `main` because it enforces `master`:

```bash
git push -u gitlab main:master
```

## 5) Authenticate GitLab HTTPS

At prompt:
- username: your GitLab login
- password: your GitLab Personal Access Token (PAT), not account password

Optional (macOS keychain):

```bash
git config --global credential.helper osxkeychain
```

## 6) Verify

```bash
git status --short --branch
git branch -vv
git remote -v
```

Expected:
- current branch is `main`
- `github` and `gitlab` remotes are present
- upstream points to the intended remote branch

## 7) Daily workflow

```bash
git add .
git commit -m "message"
git push github main
git push gitlab main
```

If GitLab uses `master`:

```bash
git push gitlab main:master
```

