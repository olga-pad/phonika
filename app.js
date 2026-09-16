'use strict';
const commons='https://commons.wikimedia.org/wiki/Special:Redirect/file/';
const sounds=[
 {letter:'s',ipa:'/s/',word:'sun',picture:'☀️',audio:commons+'Voiceless alveolar sibilant.ogg',start:0,end:.38},
 {letter:'a',ipa:'/æ/',word:'apple',picture:'🍎',audio:commons+'Near-open front unrounded vowel.ogg',start:0,end:.68},
 {letter:'t',ipa:'/t/',word:'tap',picture:'👆',audio:commons+'Voiceless alveolar plosive.ogg',start:.72,end:1.12},
 {letter:'p',ipa:'/p/',word:'pig',picture:'🐷',audio:commons+'Voiceless bilabial plosive.ogg',start:0,end:1.45},
 {letter:'i',ipa:'/ɪ/',word:'insect',picture:'🐞',audio:commons+'Near-close near-front unrounded vowel.ogg',start:0,end:.58},
 {letter:'n',ipa:'/n/',word:'nest',picture:'🪺',audio:commons+'Alveolar nasal.ogg',start:0,end:.35}
];
let i=0,player=null,stopTimer=null;
const $=id=>document.getElementById(id);
function render(){const x=sounds[i];$('letter').textContent=x.letter;$('sound').textContent=x.ipa;$('picture').textContent=x.picture;$('word').textContent=x.word;$('dots').replaceChildren(...sounds.map((_,j)=>{const d=document.createElement('span');d.className='dot'+(j===i?' on':'');return d;}));}
async function play(){const x=sounds[i];clearTimeout(stopTimer);if(player){player.pause();player=null;}player=new Audio(x.audio);player.preload='auto';try{await new Promise((resolve,reject)=>{player.addEventListener('loadedmetadata',resolve,{once:true});player.addEventListener('error',reject,{once:true});player.load();});player.currentTime=x.start||0;await player.play();if(Number.isFinite(x.end)){stopTimer=setTimeout(()=>{if(player){player.pause();player.currentTime=x.start||0;}},Math.max(60,(x.end-(x.start||0))*1000));}}catch(e){console.error('Phoneme audio could not be played',e);}}
$('letter').onclick=play;$('listen').onclick=play;$('next').onclick=()=>{clearTimeout(stopTimer);if(player)player.pause();i=(i+1)%sounds.length;render();};
render();