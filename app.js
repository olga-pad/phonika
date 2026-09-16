'use strict';
const commons='https://commons.wikimedia.org/wiki/Special:Redirect/file/';
// Each sound below is taken from the beginning of a real spoken word,
// then stopped before the following phoneme becomes prominent.
const sounds=[
 {letter:'s',ipa:'/s/',word:'sun',picture:'☀️',audio:commons+'En-us-Sun.ogg',start:.08,end:.31},
 {letter:'a',ipa:'/æ/',word:'apple',picture:'🍎',audio:commons+'En-uk-apple.ogg',start:.10,end:.39},
 {letter:'t',ipa:'/t/',word:'tap',picture:'👆',audio:commons+'En-us-tap.ogg',start:.03,end:.15},
 {letter:'p',ipa:'/p/',word:'pig',picture:'🐷',audio:commons+'Pronunciation example of the word Pig in British English.ogg',start:.03,end:.17},
 {letter:'i',ipa:'/ɪ/',word:'insect',picture:'🐞',audio:commons+'En-us-insect.ogg',start:.06,end:.27},
 {letter:'n',ipa:'/n/',word:'nest',picture:'🪺',audio:commons+'En-us-nest.ogg',start:.06,end:.29}
];
let i=0,player=null,stopTimer=null;
const $=id=>document.getElementById(id);
function render(){const x=sounds[i];$('letter').textContent=x.letter;$('sound').textContent=x.ipa;$('picture').textContent=x.picture;$('word').textContent=x.word;$('dots').replaceChildren(...sounds.map((_,j)=>{const d=document.createElement('span');d.className='dot'+(j===i?' on':'');return d;}));}
async function play(){const x=sounds[i];clearTimeout(stopTimer);if(player){player.pause();player=null;}player=new Audio(x.audio);player.preload='auto';try{await new Promise((resolve,reject)=>{player.addEventListener('loadedmetadata',resolve,{once:true});player.addEventListener('error',reject,{once:true});player.load();});player.currentTime=x.start;await player.play();stopTimer=setTimeout(()=>{if(player){player.pause();player.currentTime=x.start;}},Math.max(70,(x.end-x.start)*1000));}catch(e){console.error('Phoneme audio could not be played',e);}}
$('letter').onclick=play;$('listen').onclick=play;$('next').onclick=()=>{clearTimeout(stopTimer);if(player)player.pause();i=(i+1)%sounds.length;render();};
render();