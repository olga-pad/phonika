'use strict';
document.write('<script src="./app-core.js?v=26"><\/script>');
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
 const refreshNav=()=>{if(section==='words'){prevCard.hidden=sessionQueue.length<2;nextCard.hidden=sessionQueue.length<2;}else{prevCard.hidden=index<=0;nextCard.hidden=soundQueue.length<2;}};
 const moveWord=dir=>{if(!sessionQueue.length)return;const key=currentKey();if(key&&marked)sessionReads.set(key,Math.min(SESSION_GOAL,(sessionReads.get(key)||0)+1));const pending=sessionQueue.filter(w=>(sessionReads.get(w)||0)<SESSION_GOAL);if(!pending.length){sessionQueue=[];sessionIndex=0;document.getElementById('practice').hidden=true;document.getElementById('finish').hidden=false;return;}sessionQueue=pending;const oldPos=Math.max(0,pending.indexOf(key));sessionIndex=(oldPos+dir+pending.length)%pending.length;render();refreshNav();};
 nextCard.onclick=()=>{if(section==='words')moveWord(1);else{index=(index+1)%Math.max(1,soundQueue.length);render();refreshNav();}};
 prevCard.onclick=()=>{if(section==='words')moveWord(-1);else{index=Math.max(0,index-1);render();refreshNav();}};
 document.getElementById('wordsTab').addEventListener('click',()=>requestAnimationFrame(refreshNav));
 document.getElementById('soundsTab').addEventListener('click',()=>requestAnimationFrame(refreshNav));
 refreshNav();
});