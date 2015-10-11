var CircularShift = function(ls) {
	this.ls = ls;
	this.csLines = [];
};

CircularShift.prototype.setup = function() {
	var lines = this.ls.getLines();
	
	for (var i = 0; i < lines.length; i++) {
		this.shift(lines[i]);
	}
};

CircularShift.prototype.shift = function(line) {
	this.csLines.push(line);
	
	var words = line.split(" ");
	
	for (var i = 0; i < words.length - 1; i++) {
		
		var aux = words[0];
		
		for (var j = 0; j < words.length - 1; j++) {
			words[j] = words[j+1];
		}
		
		words[words.length - 1] = aux;

		this.csLines.push(words.join(" "));
	}
};

CircularShift.prototype.getLine = function(i){
	return this.csLines[i];
};

CircularShift.prototype.getLines = function(){
	return this.csLines;
};