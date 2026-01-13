const addBtn = document.getElementById('addSkillBtn');
const input = document.getElementById('skillInput');
const container = document.getElementById('skillsContainer');

addBtn.addEventListener('click', function() {
    const val = input.value.trim();

    if (val === "") {
        alert("请输入技能名称");
        return;
    }

    const card = document.createElement('div');
    card.className = 'skill-card';
    card.innerText = val;

    card.addEventListener('dblclick', function() {
        if (confirm(`确定要删除技能 "${val}" 吗？`)) {
            container.removeChild(this);
        }
    });

    container.appendChild(card);

    input.value = "";
    input.focus();
});

document.querySelectorAll('.skill-card').forEach(item => {
    item.addEventListener('dblclick', function() {
        container.removeChild(this);
    });
});

function sayHello() {
    alert("你好！我是李金轩，很高兴认识你！");
}