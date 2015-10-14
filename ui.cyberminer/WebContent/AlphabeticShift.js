var AlphabeticShift = function(cs) {
	this.cs = cs;
	this.asLines = [];
};

AlphabeticShift.prototype.setup = function() {
	this.asLines = this.cs.getLines().slice();
	this.asLines.sort();
};

AlphabeticShift.prototype.getLine = function(i) {
	return this.asLines[i];
};

AlphabeticShift.prototype.getLines = function() {
	return this.asLines;
};