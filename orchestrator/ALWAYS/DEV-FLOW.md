# 开发流程

## Git Flow

```
明确任务 → worktree 开发 → 测试 → PR → 合 main → 部署
```

---

## 完整开发周期

### 1. 创建 Worktree

```bash
cd repos/<repo>
git fetch origin
git worktree add ../repos/<repo>-<feature> -b feature/<name> main
cd ../repos/<repo>-<feature>

# 安装依赖（按项目技术栈）
# pnpm install / go mod download / cargo fetch / pip install -r requirements.txt
```

### 2. 开发与提交

```bash
# 检查命令（按项目技术栈）
# pnpm check / go vet ./... / cargo test / pytest
git add . && git commit -m "feat: xxx"
git push -u origin feature/<name>
```

### 3. 提交 PR

```bash
cd repos/<repo>-<feature>
gh pr create --base main --head feature/<name>
```

### 4. 合并与清理

```bash
cd repos/<repo>
gh pr merge <PR号> --merge --delete-branch
git worktree remove ../repos/<repo>-<feature>
git branch -D feature/<name>
```

### 5. 更新 Program 状态

更新 STATUS.yml，如果 Program 完成则写 workspace/RESULT.md。

---

## 分支说明

| 分支          | 用途       |
|-------------|----------|
| `main`      | 稳定版本     |
| `dev`       | 测试集成（可选） |
| `feature/*` | 功能开发     |
| `fix/*`     | Bug 修复   |

---

## Commit 规范

格式：`<type>: <description>`

| Type       | 说明     |
|------------|--------|
| `feat`     | 新功能    |
| `fix`      | Bug 修复 |
| `docs`     | 文档     |
| `refactor` | 重构     |
| `test`     | 测试     |
| `chore`    | 杂项     |

示例：

- `feat: add user authentication`
- `fix: resolve race condition in queue`
- `refactor: extract message parser`

---

## 部署（可选）

如果你有自动部署机制，在此描述。如果没有，删除此段即可。
