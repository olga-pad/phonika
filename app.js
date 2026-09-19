'use strict';
document.write('<script src="./app-core.js?v=31"><\/script>');
window.addEventListener('DOMContentLoaded',()=>{
 if(localStorage.getItem('phonika-letter-mode')!=='upper'){mode='upper';save();render();}
 const sound=document.getElementById('soundCard');
 const word=document.getElementById('wordCard');
 const picture=document.getElementById('picture');
 const mastery=document.getElementById('mastery');

 const speakAssociationWord=()=>{const key=currentKey();if(!key||!('speechSynthesis' in window))return;const text=section==='sounds'&&ASSOC[key]?ASSOC[key][0]:((currentWordObj()||{}).word||key);speechSynthesis.cancel();const u=new SpeechSynthesisUtterance(text);u.lang='en-GB';u.rate=.72;speechSynthesis.speak(u);};
 const useAudio=()=>{usedHint=true;mastery.disabled=true;speakAssociationWord();};
 const helpButton=document.getElementById('help');if(helpButton)helpButton.onclick=useAudio;
 const pictureButton=document.getElementById('showPicture');if(pictureButton)pictureButton.onclick=()=>{usedHint=true;mastery.disabled=true;picture.hidden=!picture.hidden;pictureButton.querySelector('span').textContent=picture.hidden?'Show picture':'Hide picture';};
 sound.onclick=()=>{if(section==='sounds')speakAssociationWord();};
 word.onclick=null;
 picture.onclick=null;
 const prevCard=document.getElementById('prevCard');
 const nextCard=document.getElementById('nextCard');
 const refreshNav=()=>{const count=section==='words'?sessionQueue.length:(soundSessionQueue.length?soundSessionQueue.length:soundQueue.length);const pos=section==='words'?sessionIndex:(soundSessionQueue.length?soundSessionIndex:index);prevCard.hidden=count<2||pos<=0;nextCard.hidden=count<1;};
 nextCard.onclick=()=>{if(section==='sounds'){if(!soundSessionQueue.length)startSoundSession();const key=currentKey();if(key&&marked)soundSessionReads.set(key,Math.min(SESSION_GOAL,(soundSessionReads.get(key)||0)+1));const pending=soundSessionQueue.filter(s=>(soundSessionReads.get(s)||0)<SESSION_GOAL);if(!pending.length){soundSessionQueue=[];soundSessionIndex=0;document.getElementById('practice').hidden=true;document.getElementById('finish').hidden=false;refreshNav();return;}const currentPos=pending.indexOf(key);soundSessionQueue=pending;soundSessionIndex=currentPos>=0?(currentPos+1)%pending.length:Math.min(soundSessionIndex,pending.length-1);index=soundSessionIndex;render();refreshNav();return;}next();refreshNav();};
 prevCard.onclick=()=>{if(section==='words'){if(sessionQueue.length&&sessionIndex>0){sessionIndex--;render();}}else if(soundSessionQueue.length&&soundSessionIndex>0){soundSessionIndex--;index=soundSessionIndex;render();}refreshNav();};
 document.getElementById('wordsTab').addEventListener('click',()=>requestAnimationFrame(refreshNav));
 document.getElementById('soundsTab').addEventListener('click',()=>requestAnimationFrame(refreshNav));
 refreshNav();
});