var Alphabetizer = function(cs) {
	this.cs = cs;
	this.asLines = [];
};

Alphabetizer.prototype.setup = function() {
	var circularShifts = this.cs.getLines().slice();
	
	var alphabetizedLines = this.shift(circularShifts);
	
	this.setLines(alphabetizedLines);
};

Alphabetizer.prototype.shift = function(lines) {
	return lines.sort();
};

Alphabetizer.prototype.getLines = function() {
	return this.asLines;
};

Alphabetizer.prototype.setLines = function(lines){
	this.asLines = lines;
};
