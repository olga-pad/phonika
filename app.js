'use strict';
const sounds=[
 {letter:'s',word:'sun',picture:'☀️',phoneme:'ssss'},
 {letter:'a',word:'apple',picture:'🍎',phoneme:'apple'},
 {letter:'t',word:'tap',picture:'👆',phoneme:'t'},
 {letter:'p',word:'pig',picture:'🐷',phoneme:'p'},
 {letter:'i',word:'insect',picture:'🐞',phoneme:'insect'},
 {letter:'n',word:'nest',picture:'🪺',phoneme:'nnnn'}
];
let i=0;
const $=id=>document.getElementById(id);
function render(){const x=sounds[i];$('letter').textContent=x.letter;$('picture').textContent=x.picture;$('word').textContent=x.word;$('dots').replaceChildren(...sounds.map((_,j)=>{const d=document.createElement('span');d.className='dot'+(j===i?' on':'');return d;}));}
function speak(text){if(!('speechSynthesis' in window))return;speechSynthesis.cancel();const u=new SpeechSynthesisUtterance(text);u.lang='en-GB';u.rate=.68;u.pitch=1;speechSynthesis.speak(u);}
function playPhoneme(){const x=sounds[i];if(x.letter==='a'||x.letter==='i'){const u=new SpeechSynthesisUtterance(x.word);u.lang='en-GB';u.rate=.68;u.volume=0;speechSynthesis.cancel();/* Isolated vowel TTS is unreliable; keep letter interaction silent rather than teach a letter name. */return;}speak(x.phoneme);}
function playWord(){speak(sounds[i].word);}
$('letter').onclick=playPhoneme;$('picture').onclick=playWord;$('next').onclick=()=>{speechSynthesis?.cancel();i=(i+1)%sounds.length;render();};
render();