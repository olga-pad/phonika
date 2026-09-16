'use strict';
const sounds=[
 {letter:'s',ipa:'/s/',word:'sun',picture:'☀️',speech:'ssss'},
 {letter:'a',ipa:'/æ/',word:'apple',picture:'🍎',speech:'a'},
 {letter:'t',ipa:'/t/',word:'tap',picture:'👆',speech:'t'},
 {letter:'p',ipa:'/p/',word:'pig',picture:'🐷',speech:'p'},
 {letter:'i',ipa:'/ɪ/',word:'insect',picture:'🐞',speech:'i'},
 {letter:'n',ipa:'/n/',word:'nest',picture:'🪺',speech:'nnnn'}
];
let i=0;
const $=id=>document.getElementById(id);
function render(){const x=sounds[i];$('letter').textContent=x.letter;$('sound').textContent=x.ipa;$('picture').textContent=x.picture;$('word').textContent=x.word;$('dots').replaceChildren(...sounds.map((_,j)=>{const d=document.createElement('span');d.className='dot'+(j===i?' on':'');return d;}));}
function play(){const x=sounds[i];if(!('speechSynthesis'in window))return;speechSynthesis.cancel();const u=new SpeechSynthesisUtterance(x.speech);u.lang='en-GB';u.rate=.65;u.pitch=1;speechSynthesis.speak(u);}
$('letter').onclick=play;$('listen').onclick=play;$('next').onclick=()=>{i=(i+1)%sounds.length;render();};
render();