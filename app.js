'use strict';
document.write('<script src="./app-core.js?v=24"><\/script>');
window.addEventListener('DOMContentLoaded',()=>{
 // Use uppercase print letters by default for new users. Keep an explicit saved choice.
 if(!localStorage.getItem('phonika-letter-mode')){mode='upper';save();render();}
 const card=document.getElementById('soundCard');
 card.addEventListener('click',()=>{if(section==='sounds'){usedHint=true;document.getElementById('mastery').disabled=true;speak();}});
});