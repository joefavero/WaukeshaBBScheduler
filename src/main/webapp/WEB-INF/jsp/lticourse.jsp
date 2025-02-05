<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" href="/TulsaTechPortal/static/css/style.css" type="text/css">
<script>
function setDates(value) {
	var s1start = document.getElementById('semester1start').value.substring(0,10);
	var s1end = document.getElementById('semester1end').value.substring(0,10);
	var s2start = document.getElementById('semester2start').value.substring(0,10);
	var s2end = document.getElementById('semester2end').value.substring(0,10);

	if(value == 'semester1') {
           document.getElementById('start').value = s1start;
	       document.getElementById('end').value = s1end;
	} else if (value == 'semester2') {
	       document.getElementById('start').value = s2start;
	       document.getElementById('end').value = s2end;
	}
}
</script>

<meta charset="UTF-8">
<title>Tulsa Tech Course Progress Tool</title>
</head>
<body>
<div class="content-box-gray ">
    <div class="title">Course Progress Report Tool</div>

    <div class="content">
    	<ul>
		<li>Teacher ID: ${username}</li>
		<li>Course ID: ${courseid}</li>
		<li>Teacher Email: ${email}</li>
	</ul>


	<form method="POST" action="generateCourseReport">  
		<input type="hidden" name="username" value="${username}" />
		<input type="hidden" name="courseid" value="${courseid}" />
		<input type="hidden" name="email" value="${email}" />
		<input type="hidden" name="teachername" value="${teachername}" />
		<input type="hidden" name="term" value="${term}" />
		<input type="hidden" id="semester1start" name="semester1start" value="${semester1start}" />
		<input type="hidden" id="semester1end" name="semester1end" value="${semester1end}" />
		<input type="hidden" id="semester2start" name="semester2start" value="${semester2start}" />
		<input type="hidden" id="semester2end" name="semester2end" value="${semester2end}" />
		<input type="hidden" id="ltiurl" name="ltiurl" value="${ltiurl}" />
		<label>Select Semester</label>
		<select name="semester" id="semester-select" onchange='setDates(this.value);'>
   			<option value="">--Please choose an option--</option>
    		<option value="semester1">Semester 1</option>
    		<option value="semester2">Semester 2</option>	
    	</select>
    	<br>
    	<label>Start Date</label>
		<input type="date" id="start" name="start-date" required>
    	<label>End Date</label>
    	<input type="date" id="end" name="end-date" required>
		<br>
		<input type="submit" value="Submit" class="button1">  
		<input type="button" value="Close" onclick="javascript:window.close('','_parent','');">  
	</form>  
	</div>
	
</body>
</html>