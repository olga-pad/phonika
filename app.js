'use strict';
const sounds=[
 {letter:'s',word:'sun',picture:'☀️'},
 {letter:'a',word:'apple',picture:'🍎'},
 {letter:'t',word:'tap',picture:'👆'},
 {letter:'p',word:'pig',picture:'🐷'},
 {letter:'i',word:'insect',picture:'🐞'},
 {letter:'n',word:'nest',picture:'🪺'}
];
let i=0;
const $=id=>document.getElementById(id);
function render(){const x=sounds[i];$('letter').textContent=x.letter;$('sound').textContent='';$('picture').textContent=x.picture;$('word').textContent=x.word;$('dots').replaceChildren(...sounds.map((_,j)=>{const d=document.createElement('span');d.className='dot'+(j===i?' on':'');return d;}));}
function playWord(){const x=sounds[i];if(!('speechSynthesis' in window))return;speechSynthesis.cancel();const u=new SpeechSynthesisUtterance(x.word);u.lang='en-GB';u.rate=.72;u.pitch=1;speechSynthesis.speak(u);}
$('letter').onclick=playWord;$('listen').onclick=playWord;$('picture').onclick=playWord;$('word').onclick=playWord;$('next').onclick=()=>{speechSynthesis?.cancel();i=(i+1)%sounds.length;render();};
render();