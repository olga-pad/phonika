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
});