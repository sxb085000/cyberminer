var LineStorage = function () {
	this.lines = [];
};

LineStorage.prototype.setLines = function(lines){
	this.lines = lines;
};

LineStorage.prototype.getLine = function(i){
	return this.lines[i];
};

LineStorage.prototype.getLines = function(){
	return this.lines;
};