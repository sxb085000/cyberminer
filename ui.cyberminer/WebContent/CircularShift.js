var CircularShift = function(ls) {
	this.ls = ls;
	this.csLines = [];
};

CircularShift.prototype.setup = function() {
	var lines = this.ls.getLines();
	
	var circularShifts = [];
	for (var i = 0; i < lines.length; i++) {
		circularShifts = circularShifts.concat(this.shift(lines[i].trim().replace(/  +/g, ' ')));
	}
	
	this.setLines(circularShifts);
};

CircularShift.prototype.shift = function(line) {
	var circularShifts = [];
	circularShifts.push(line);
	
	var words = line.split(" ");
	
	for (var i = 0; i < words.length - 1; i++) {
		
		var aux = words[0];
		
		for (var j = 0; j < words.length - 1; j++) {
			words[j] = words[j+1];
		}
		
		words[words.length - 1] = aux;

		circularShifts.push(words.join(" "));
	}
	
	return circularShifts;
};

CircularShift.prototype.getLines = function(){
	return this.csLines;
};

CircularShift.prototype.setLines = function(lines){
	this.csLines = lines;
};