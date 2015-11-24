var Input = function (ls) {
	this.ls = ls;
};

Input.prototype.read = function() {
	this.setLines(this.medium());
};

Input.prototype.setLines = function(lines) {
	this.ls.setLines(lines);
};

Input.prototype.medium = function () {
	return $("#input").val().split("$");
};