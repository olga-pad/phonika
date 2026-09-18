'use strict';
document.write('<script src="./app-core.js?v=27"><\/script>');
window.addEventListener('DOMContentLoaded',()=>{
 if(localStorage.getItem('phonika-letter-mode')!=='upper'){mode='upper';save();render();}
 const sound=document.getElementById('soundCard');
 const word=document.getElementById('wordCard');
 const picture=document.getElementById('picture');
 const mastery=document.getElementById('mastery');

 const useAudio=()=>{usedHint=true;mastery.disabled=true;speak();};
 sound.onclick=null;
 word.onclick=null;
 picture.onclick=null;
 const prevCard=document.getElementById('prevCard');
 const nextCard=document.getElementById('nextCard');
 const refreshNav=()=>{const count=section==='words'?sessionQueue.length:soundQueue.length;prevCard.hidden=count<2;nextCard.hidden=count<2;};
 nextCard.onclick=()=>{next();refreshNav();};
 prevCard.onclick=()=>{if(section==='words'){if(sessionQueue.length){sessionIndex=(sessionIndex-1+sessionQueue.length)%sessionQueue.length;render();}}else{index=(index-1+Math.max(1,soundQueue.length))%Math.max(1,soundQueue.length);render();}refreshNav();};
 document.getElementById('wordsTab').addEventListener('click',()=>requestAnimationFrame(refreshNav));
 document.getElementById('soundsTab').addEventListener('click',()=>requestAnimationFrame(refreshNav));
 refreshNav();
});