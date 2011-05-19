function addElement() {
  var ni = document.getElementById('fileDivId');
  var lastInputId = ni.lastChild.id;
  regex = /Upload(\d+)File/;
  var numLast = lastInputId.match(regex);
  var num = parseInt(numLast[1]) + 1;
  var newInput = document.createElement('input');
  newInput.id = 'Upload'+num+'File';
  newInput.type = 'file';
  newInput.name = 'data[Upload]['+num+'][file]';
  ni.appendChild(newInput);
}