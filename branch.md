## 版本控制说明
分支：
main：用于存放稳定的发布版本。
develop：用于存放最新的开发代码。
feature：用于开发特性或修复BUG，完成后合并回开发分支。

命名约定：
分支命名为develop/功能名称 (例如：feature/login)

版本标签：Major.Minor.Patch (例如：1.0.0)。
其中Major为主版本号 ，引入重大功能或架构变更，例如增加全新的健康监控模块。Minor为次版本号，添加新的功能或对现有功能进行增强，不破坏向后兼容性，例如新增训练计划模块。Patch为补丁号，修复Bug或进行小幅度的调整，如修正UI问题或小的逻辑错误。

分支策略：
所有开发人员在创建新功能或修复时，需从 develop 分支创建新分支。功能完成后，提交合并请求（Pull Request）到 develop 分支，进行代码评审和自动测试。当 develop 分支足够稳定时，合并到 main 分支，并发布新版本。


仓库地址：https://github.com/Tniopxist/YueDong.git

git使用说明：
仓库地址：https://github.com/Tniopxist/YueDong.git

初始化：
git init
git remote add origin https://github.com/Tniopxist/YueDong.git

克隆：
git clone https://github.com/Tniopxist/YueDong.git

配置：
git config --global user.name "[name]"
git config --global user.email "[email]"

推送：
git add 文件名
git commit -m "添加某功能" # 填写详细提交的日志说明
git branch -M main
git push <远程主机名> <本地分支名>

拉取：
git pull

打tag:
git tag v1.0.0

分支操作：
git branch [branch-name] # 新建一个分支，但依然停留在当前分支
git checkout -b [branch] # 新建一个分支，并切换到该分支
git merge [branch] # 合并指定分支到当前分支
