'use strict';
document.write('<script src="./app-core.js?v=23"><\/script>');
window.addEventListener('DOMContentLoaded',()=>{
 const card=document.getElementById('soundCard');
 card.addEventListener('click',()=>{if(section==='sounds'){usedHint=true;document.getElementById('mastery').disabled=true;speak();}});
});