import { initializeApp } from 'https://www.gstatic.com/firebasejs/10.7.0/firebase-app.js';
import { getAuth } from 'https://www.gstatic.com/firebasejs/10.7.0/firebase-auth.js';
import { getFirestore, doc, collection, getDocs, setDoc, getCountFromServer } from 'https://www.gstatic.com/firebasejs/10.7.0/firebase-firestore.js';
import { getDatabase, set, ref as sRef } from "https://www.gstatic.com/firebasejs/10.7.0/firebase-database.js";
import { getDownloadURL, getStorage, ref as fbstorageRef, uploadBytes } from "https://www.gstatic.com/firebasejs/10.7.0/firebase-storage.js";


const firebaseConfig = {
  apiKey: "AIzaSyDWRGsRXftk-6G5ZkjFCB8-XIPXt-ywGJ0",
  authDomain: "marketdb-final.firebaseapp.com",
  projectId: "marketdb-final",
  storageBucket: "marketdb-final.appspot.com",
  messagingSenderId: "45053507579",
  appId: "1:45053507579:web:35cd268bf70012285d3d5e"
};



const myApp = initializeApp(firebaseConfig);
window.db = getFirestore(myApp);
window.imgHost = getStorage(myApp);
const imgHost = getStorage(myApp);
const db = getFirestore(myApp);
var globalContainerSelct = 'trendng-item'; //for item-container rendering algorithm,, access later.
var globalItemTag = 'trendng-items';
const globalDbCollIds = ['trendng-items','tech-items','sport-items','pet-items','food-items','cleanng-items']; //const for reason do not edit this code map to another element.

let glblHtmlMap = new Map();



window.onload = function () {
  $('#lgn-pane').hide();
  $('#itemForm').hide();
  initScroll(); //reinvoke when interface refreshes
};



class item {
  constructor(name, price, imgpath, description, quantity, seller, categories, ID, fulldbpath) {
    this.name = name;
    this.price = price;
    this.img = imgpath; // storage bucket + '/' + fileid.jpg <- file.name 
    this.description = description;
    this.quantity = quantity;
    this.seller = seller;
    this.tag = categories;
    this.itemID = ID;
    this.fulldbpath = fulldbpath;
  }

}

const itemConverter = { //--updated to match new field
  toFirestore: (item) => { //store by item
    return {
      name: item.name,
      price: item.price,
      img: item.img,
      description: item.description,
      quantity: item.quantity,
      seller: item.seller,
      tag: item.tag,
      itemID: item.itemID,
      fulldbpath: item.fulldbpath
    };
  },
  fromFirestore: (snapshot, options) => {
    const data = snapshot.data();

    return new item(data.name, data.price, data.img, data.description, data.quantity, data.seller, data.tag, data.itemID, data.fulldbpath);
  }
};

async function castImgUrl(item,file) {
  var itemPathRef = fbstorageRef(imgHost,`${item.tag}${item.itemID}`);
  try {
    await uploadBytes(itemPathRef, file); 
  } catch (err) {
    console.log('error with castImgUrl (interface for firebase storage api): ' + err);
    return;
  }

  try {
    const itemImgLnk = await getDownloadURL(itemPathRef);
    item.img = itemImgLnk;
    return itemImgLnk; 
  } catch (e) {
    console.log("Cant download image: ", e);
    return "";
  }
}

async function generateSequentialId() { //id for database
  const techItems = collection(db, 'tech-items');
  const trendngItems = collection(db, 'trendng-items');
  const foodItems = collection(db, 'food-items');
  const cleanngItems = collection(db, 'cleanng-items');

  const snapshot = await getCountFromServer(techItems);
  const snapshot1 = await getCountFromServer(trendngItems);
  const snapshot2 = await getCountFromServer(foodItems);
  const snapshot3 = await getCountFromServer(cleanngItems);

  var totalcount = Number(snapshot.data().count + snapshot1.data().count + snapshot2.data().count + snapshot3.data().count);

  var nextIndex = totalcount + 1;
  return nextIndex;
}
$('#itemUploadForm').submit(function (event) {
  event.preventDefault(); // Prevent default form submission

  // form input
  const itemName = $('#itemNameInput').val();
  const itemPrice = parseFloat($('#itemPriceInput').val()).toFixed(2);
  const itemDescription = $('#itemDescriptionInput').val();
  const itemQuantity = $('#itemQuantityInput').val();

  // img upload input
  const file1 = $('#itemImgInput')[0].files[0]; //add restrictions to the file size in this area.

  const maxSize = 5120000; // 5000kb limit.
  if (file1 && file1.size > maxSize) {
    alert('File size exceeds the limit (5000kb). Please select a smaller file.');
    return;
  }
  //category input:

  //ensure datatype before storage
  var itemdescrSubmit = String(itemDescription);
  var itempriceSubmit = itemPrice;
  var itemtagSubmit = globalItemTag; //local instance
  var itemquanSubmit = Number(itemQuantity);
  var idforupload = 0;
  //create id
  // this is really dense, note for me -> .then calls the async function after gen id returns, and casts 
  generateSequentialId().then(async (nextAvalInd) => {
    idforupload = Number(nextAvalInd);


    if (isNaN(itempriceSubmit) || itempriceSubmit < 0 || isNaN(itemquanSubmit) || itemquanSubmit < 0 || !Number.isInteger(parseFloat(itemQuantity))) {
      console.error('Invalid price or quantity field');
      alert('invalid input.');
      return;
    }
    
    var itemForUpload = new item(itemName, itempriceSubmit, "nooo", itemdescrSubmit, itemquanSubmit, "admin-vendor", itemtagSubmit, idforupload, "fulldbpathplaceholder");
    const ul = await castImgUrl(itemForUpload,file1);
    itemForUpload.img = ul;
    addItemToDB(itemForUpload, file1);
    $('#itemForm').hide();

    //if success toggle alert: 

  }).catch((er) => {
    console.log('Promise rejected within form event: ', er);
  });

});
async function addItemToDB(item) {
  try {
    item.fulldbpath = item.tag + '/' + String(item.itemID);
    var pathforusehere = item.fulldbpath;
    
    const ref = await doc(db, pathforusehere);


    await setDoc(ref, itemConverter.toFirestore(item));
    console.log("Document written with path: ", item.fulldbpath);
    console.log("Document written with img url:", item.img);

  } catch (err) {
    console.log('Error on Db side ( i think thats where this note goes) ', err);
  }
}

let globalhtmlMap = new Map();



async function containerHtmlGen(){
  let runningHtmlContent = "";
  let promHdl = []
  const categories = globalDbCollIds;
  for(const colliD of categories){
        try{
        let runningCollRef = collection(db,colliD);
        await getDocs(runningCollRef).then( (dcs) => {
        dcs.forEach((docSnap) => {
        promHdl.push(docSnap);
        let fakeItem = docSnap.data(); //has db fields which are item fields, i dont think im using a converter on retrieval, couldnt figure out the api for that but it doesnt matter. i dont have any custom methods on item so all data is preserved.
        runningHtmlContent += `<div class="item" id="${fakeItem.name}${String(fakeItem.itemID)}"><h1>${fakeItem.name}</h1> <img class="item-image" src="${fakeItem.img}"></img><p>${fakeItem.name}</p><p>${String(fakeItem.price)}</p><p style="font-size:10xp,overflow: hidden;">${fakeItem.description}</p> <button class="add-items-to-cart authUserOnly" id="addToCart">Add To Cart</button> </div>`;
      }); 
    });} catch(e) {
      console.log('error retrieving data: ', e);
    }
    /*
    docsnaps.forEach((docSnap) => { //may need an async here <-- and an await before docsnap.data().. if this starts throwing undefined values.
      let fakeItem = docSnap.data(); //has db fields which are item fields, i dont think im using a converter on retrieval, couldnt figure out the api for that but it doesnt matter. i dont have any custom methods on item so all data is preserved.
      runningHtmlContent += `<div class="item" id="${fakeItem.name}${String(fakeItem.itemID)}"><h1>${fakeItem.name}</h1> <img class="item-image" src="${fakeItem.img}"></img><p>${fakeItem.name}</p><p>${String(fakeItem.price)}</p><p style="font-size:10xp">${fakeItem.description}</p> <button class="add-items-to-cart authUserOnly" id="addToCart">Add To Cart</button> </div>`;
    })
    */


    let dispText = "";
    switch(colliD) {
      case 'trendng-items': dispText = 'Trending Products'; break;
      case 'tech-items': dispText = 'Tech Products'; break;
      case 'sport-items': dispText = 'Athletic Products'; break;
      case 'pet-items': dispText = 'Pet Care Products'; break;
      case 'food-items': dispText = 'Grocery Items'; break;
      case 'cleanng-items': dispText = 'Home Improvement'; break;
    }
    let htmlForMap = `<div class="item-container" id="${colliD}-container" name="${colliD}"><p class="stdTxtTemplate">${dispText}</p>${runningHtmlContent}</div>`;
    
    runningHtmlContent = '';
    await Promise.resolve(promHdl).then(()=>{
      globalhtmlMap.set(colliD,htmlForMap);
    }); 
  }
}

function renderLayout() { //currently reading global map item.
  $('#customContainer').html('');
  var finalHtmlOrderForDisplay = '';
  let n = ['trendng-items','tech-items','cleanng-items','pet-items','food-items','sport-items'];
  let opt2 = ['trendng-items','tech-items','sport-items','pet-items','food-items','cleanng-items'];
  console.log(globalContainerSelct);
  
  
  /*why does this work??  */
  if (globalContainerSelct == n[0]){ //if display algorithm = start with trend
    for (var i of n){
      console.log(`layout if trending top:`,glblHtmlMap.get(i));
      finalHtmlOrderForDisplay += globalhtmlMap.get(i);     //do normal order
    }
  } else if(globalContainerSelct != 'trendng-items') {//display alg does not start with trend
    finalHtmlOrderForDisplay += globalhtmlMap.get(globalContainerSelct);
    for (var i of opt2){
      console.log('layout if trending is not top');
      finalHtmlOrderForDisplay += globalhtmlMap.get(i);
      console.log(finalHtmlOrderForDisplay);
    }
  }
  
  return finalHtmlOrderForDisplay;
}
async function correctInit(){
  await containerHtmlGen().then(() => {
    $('#customContainer').html(renderLayout());
  }).then(()=>{
    initScroll();
  });
  
}



$('#tag-selectorTrending').on('click',async () =>{
  $(this).addClass('selected');
  globalContainerSelct = 'trendng-items';
  await correctInit();
  $(this).removeClass('selected'); //stays selected while catching promise.. kind of neat for a short highlight.
}); 
$('#tag-selectorTech').on('click',async () =>{
  $(this).addClass('selected');
  globalContainerSelct = 'tech-items';
  await correctInit();
  $(this).removeClass('selected'); //stays selected while catching promise.. kind of neat for a short highlight.
}); 
$('#tag-selectorCleaning').on('click',async () =>{
  $(this).addClass('selected');
  globalContainerSelct = 'cleanng-items';
  await correctInit();
  $(this).removeClass('selected'); //stays selected while catching promise.. kind of neat for a short highlight.
}); 
$('#tag-selectorPet').on('click',async () =>{
  $(this).addClass('selected');
  globalContainerSelct = 'pet-items';
  await correctInit();
  $(this).removeClass('selected'); //stays selected while catching promise.. kind of neat for a short highlight.
}); 
$('#tag-selectorFood').on('click',async () =>{
  $(this).addClass('selected');
  globalContainerSelct = 'food-items';
  await correctInit();
  $(this).removeClass('selected'); //stays selected while catching promise.. kind of neat for a short highlight.
}); 
$('#tag-selectorSport').on('click',async () =>{
  $(this).addClass('selected');
  globalContainerSelct = 'sport-items';
  await correctInit();
  $(this).removeClass('selected'); //stays selected while catching promise.. kind of neat for a short highlight.
}); 
$('#refreshShop').on('click',async function() {
  console.log('reg');
  correctInit();
  $('#refreshShop').toggleClass('selected');
});

$('#additemsBtn').on('click', function () {
  var itmform = $('#itemForm');

  if (itmform.css('display') === 'none' || !itmform.is(':visible')) {
    itmform.show();
    console.log('item is currently showing, click to hide');
  } else {
    itmform.hide();
    console.log('item is currently hidden, click to show');
  }
});
$('.selectCats').on('click', function () {
  $('.selectCats').removeClass('selected');
  $('this').toggleClass('selected');
  globalItemTag = $(this).attr('name'); //this line is all that is necessary to properly convert from user button input to item.tag (used in db path and treated as string in most of this code) -- this is due to the structure of the code.

  console.log(globalItemTag);
});
//layout:



function initScroll() { //this used to work so that you can scroll vertically when your mouse leaves the item pane divs, like i talked about in class it was seemless and clean, but now it disables scrolling on the html bc of
  var globalInScrollDiv = false;
  var vertscroll = false;
  var activemouseelement = document.getElementById('shopContainer');
  document.querySelectorAll('.item-container').forEach((element) => { //working but syncs them <- nevermind i fixed it :)
    element.addEventListener('mouseover', (e1) => {
      vertscroll = true;
      globalInScrollDiv = true;
      console.log(`in scroll div`);
      activemouseelement = e1.target; //assigns div to be scrolled in based on the closest div to the event, assigns when the scroll event happens, and before the event is prevented and modified.
    });
  });
  document.querySelectorAll('.item-container').forEach((element) => {
    element.addEventListener('mouseleave', () => {
      vertscroll = false;
      activemouseelement = "html";
      globalInScrollDiv = false;
      console.log('leaving scroll div');
    });
  });
  window.addEventListener('wheel',
    (eventloc) => {
      document.querySelectorAll('.item-container').forEach((element) => {

        if (activemouseelement != "") {
          if (vertscroll){
            eventloc.preventDefault();
            activemouseelement.scrollLeft += (eventloc.deltaY) / 5; //slow scroll sideways scroll in item containers.
            console.log('sideways scroll');
          }
        } else {        }


      });
    }, { passive: false }
  );
}















$('#navbtn').on('click', function () {

  var lgnPane = $('#lgn-pane');

  if (lgnPane.css('display') === 'none' || !lgnPane.is(':visible')) {
    lgnPane.show();
    console.log('item is currently showing, click to hide');
  } else {
    lgnPane.hide();
    console.log('item is currently hidden, click to show');
  }
});

$('#lgnBtn').on('click', function () {

  $("#verifDisplay").removeClass('incorrectLogin');
  var userInput = $('#email').val();
  var passwordInput = $('#password').val();


  validateUser(userInput);
  validatePW(passwordInput);


  function validateUser(userInput) {
    var validUser = "explicitly"; //replace with boolean command to check if db contains value for user -- or condense code to remove these 2 variables.
    var validEmail = "dylanrobertboblott@gmail.com"; //replace with boolean command to check if db contains value for email
    (validUser == userInput.toLowerCase() || validEmail == userInput.toLowerCase) ? validatePW() : ifInvalid(userInput);
  }
  function validatePW(passwordInput) {
    var validPW = "P@ssw0rd!"; //replace with db pull for pw, or condense code to remove this variable.
    (validPW == passwordInput) ? ifValid() : ifInvalid(passwordInput);
  }

  function ifValid() {
    $('#lgn-pane').hide();
  }

  function ifInvalid(userInput) {
    $('#verifDisplay').addClass('incorrectLogin');
    $('#verifDisplay').text("Incorrect Username or Email,\nPlease try again.");
  }
  function ifInvalid(passwordInput) { //.incorrectPW
    $('#verifDisplay').addClass('incorrectLogin');
    $('#verifDisplay').text("Incorrect Password,\nPlease try again.");
  }
  /*
    -pull from db+
    -check if match for username *OR* email
    -password match check
    -refresh page attributes if login successful (cart, profile, etc?)
    - if successful login: $('lgn-pane').css('opacity','0');
  */
});
$('#pwbtn').on('click', function () {
  ($('#password').attr('type') === 'password') ? showPW() : hidePW();


  function showPW() {
    $('#password').attr('type', 'text');
    $('#pwbtn').removeClass('eye-slash').addClass('eye');
  }
  function hidePW() {
    $('#password').attr('type', 'password');
    $('#pwbtn').removeClass('eye').addClass('eye-slash');
  }
});


$('.close').click(function () {
  $('#itemForm').hide();
});









// this is the start of the older site code, that i need to check the accuracy of.
//////////////////////////////////////////////////////////////=================================================================================================================================================










