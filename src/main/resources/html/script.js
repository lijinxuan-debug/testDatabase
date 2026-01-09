// 1. 获取 DOM 元素引用
const addBtn = document.getElementById('addSkillBtn');
const input = document.getElementById('skillInput');
const container = document.getElementById('skillsContainer');

// 2. 点击按钮添加技能
addBtn.addEventListener('click', function() {
    const val = input.value.trim();

    if (val === "") {
        alert("请输入技能名称");
        return;
    }

    // 创建新的 DOM 节点
    const card = document.createElement('div');
    card.className = 'skill-card';
    card.innerText = val;

    // 为动态生成的元素绑定双击事件 (Double Click)
    card.addEventListener('dblclick', function() {
        if (confirm(`确定要删除技能 "${val}" 吗？`)) {
            container.removeChild(this); // 移除节点
        }
    });

    // 插入到容器中
    container.appendChild(card);

    // 清空输入框并聚焦
    input.value = "";
    input.focus();
});

// 3. 为页面已有的技能卡片也绑定双击删除
document.querySelectorAll('.skill-card').forEach(item => {
    item.addEventListener('dblclick', function() {
        container.removeChild(this);
    });
});

// 4. 简单的打招呼函数
function sayHello() {
    alert("你好！我是李金轩，很高兴认识你！");
}