'use strict';
document.write('<script src="./app-core.js?v=28"><\/script>');
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
 const refreshNav=()=>{const count=section==='words'?sessionQueue.length:soundQueue.length;const pos=section==='words'?sessionIndex:index;prevCard.hidden=count<2||pos<=0;nextCard.hidden=count<2;};
 nextCard.onclick=()=>{if(section==='words'){if(!sessionQueue.length)return;const key=currentKey();if(key&&marked)sessionReads.set(key,Math.min(SESSION_GOAL,(sessionReads.get(key)||0)+1));const pending=sessionQueue.filter(w=>(sessionReads.get(w)||0)<SESSION_GOAL);if(!pending.length){sessionQueue=[];sessionIndex=0;document.getElementById('practice').hidden=true;document.getElementById('finish').hidden=false;return;}sessionQueue=pending;sessionIndex=Math.min(sessionIndex+1,pending.length-1);render();}else{index=Math.min(index+1,Math.max(0,soundQueue.length-1));render();}refreshNav();};
 prevCard.onclick=()=>{if(section==='words'){if(sessionQueue.length&&sessionIndex>0){sessionIndex--;render();}}else if(index>0){index--;render();}refreshNav();};
 document.getElementById('wordsTab').addEventListener('click',()=>requestAnimationFrame(refreshNav));
 document.getElementById('soundsTab').addEventListener('click',()=>requestAnimationFrame(refreshNav));
 refreshNav();
});