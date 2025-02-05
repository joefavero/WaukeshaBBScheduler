<!DOCTYPE html>
<html>

<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.13.0/css/all.min.css">
    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            padding: 0;
            font-family: 'Roboto', sans-serif;
        }

        header {
            width: 100%;
            height: fit-content;
            display: flex;
            align-items: center;
            background-color: #353B48;
            margin: 0;
        }

        header img {
            margin: 16px 24px 12px;

        }

        h1 {
            font-family: 'Roboto', sans-serif;
            font-style: normal;
            font-weight: 700;
            font-size: 22px;
            /* line-height: 26px; */
            margin: 40px 24px 24px;
        }

        p {
            font-size: 16px;
            font-weight: 700;
            margin-top: 0;
        }




        /* Style all input fields */
        input {
            width: 100%;
            padding: 12px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-sizing: border-box;
            margin-top: 2px;
            margin-bottom: 16px;
        }

        /* Style the submit button */
        input[type=submit] {
            background-color: #FF4A3D;
            color: white;
            font-weight: 700;
            border-radius: 4px;
            border: 0;
            width: 140px;
            height: 44px;
            align-self: end;
            margin: 24px 0 0 0px;
        }
		input[type=submit]:hover {
            background-color: #e24136;
        }
 
        input[type=submit]:active {
            background-color: #fe5c51;
        }
 
        input[type=submit]:focus {
            background-color: #FF4A3D;
        }
 
        input[type=submit]:disabled {
            background-color: #ccc;
        }
 

        /* Style the container for inputs */
        .container {
            max-width: 800px;
            margin: 0 24px;
            border-radius: 12px;
            border: 2px solid #E0E0E0;
            background-color: #fff;
            padding: 24px;
        }

        label {
            font-size: .75rem;
            background-color: #fff;
            padding: 2px 4px;



        }

        /* extra element to position message box and submit button */
        .instuction-submit {
            display: flex;
            flex-wrap: wrap;
            justify-content: space-between;
            margin-top: 12px;
        }

        /* The message box is shown when the user clicks on the password field */
        /* EH-Please leave message visible */
        #message {
            /* display: none; */
            background: #dbdbdb0f;
            border: 1.5px solid #FF4A3D;
            border-radius: 6px;
            position: relative;
            padding: 8px 16px;
            margin-right: 10px;
        }

        #message h3 {
            font-size: 14px;
            margin: 8px 0 12px;
        }

        #message p {
            margin: 0 0 6px 10px;
            font-size: 14px;

        }

        /* Add a green text color and a checkmark when the requirements are right */
        .valid {
            color: green;
        }

        .valid:before {
            position: relative;
            left: -8px;
            /* content: "âœ”"; */
            content: '\2713';
        }

        /* Add a red text color and an "x" when the requirements are wrong */
        .invalid {
            color: #FF4A3D;
        }

        .invalid:before {
            position: relative;
            left: -8px;
            /* content: "âœ–"; */
            content: '\00D7';
        }
    </style>
</head>

<body>
    <header>
        <img src="https://tulsatech.edu/_layouts/bb/TulsaTechPRTLogo.svg" alt="Tulsa Tech Progress Report Tool Logo">
    </header>
    <h1>Parent/Guardian Account Registration</h1>


    <div class="container">
        <p>Welcome! Please create an account to access the Tulsa Tech Progress Report Tool.</p>
        <form action="addParentRegistration">
            <input type="hidden" name="studentId" value="${studentId}" />

            <label for="usrname"><b>Username</b></label>
            <input type="text" id="username" name="username" value="${username}" required readonly>

            <label for="psw"><b>Password</b></label>
            <i class="far fa-eye" id="togglePassword" style="cursor: pointer;"></i>
            <input type="password" id="psw" name="psw" pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}"
                placeholder="Must contain at least one number and one uppercase and lowercase letter, and at least 10 or more characters"
                required autocomplete="current-password">
     

            <label for="firstName"><b>First Name</b></label>
            <input type="text" id="firstname" name="firstname" placeholder="Enter First Name" required>

            <label for="lastName"><b>Last Name</b></label>
            <input type="text" id="lastname" name="lastname" placeholder="Enter Last Name" required>

            <!-- EH Moved #message to inside form. Extra div to help with positioning.-->
            <div class="instuction-submit">
                <div id="message">
                    <h3>Passwords must contain the following:</h3>
                    <p id="letter" class="invalid">A <b>lowercase</b> letter</p>
                    <p id="capital" class="invalid">A <b>capital (uppercase)</b> letter</p>
                    <p id="number" class="invalid">A <b>number</b></p>
                    <p id="length" class="invalid">Minimum <b>10 characters</b></p>
                </div>
                <input type="submit" value="Create Account">
                
            </div>
        </form>
    </div>



    <script>
    
    const togglePassword = document.querySelector('#togglePassword');
    const password = document.querySelector('#psw');

    togglePassword.addEventListener('click', function (e) {
      // toggle the type attribute
      const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
      password.setAttribute('type', type);
      // toggle the eye slash icon
      this.classList.toggle('fa-eye-slash');
  });
    
        var myInput = document.getElementById("psw");
        var letter = document.getElementById("letter");
        var capital = document.getElementById("capital");
        var number = document.getElementById("number");
        var length = document.getElementById("length");


        // EH - Can we leave the message box visible at all times?

        // When the user clicks on the password field, show the message box
        // myInput.onfocus = function () {
        //     document.getElementById("message").style.display = "block";
        // }

        // When the user clicks outside of the password field, hide the message box

        // myInput.onblur = function () {
        //     document.getElementById("message").style.display = "none";
        // }

        // When the user starts to type something inside the password field
        myInput.onkeyup = function () {
            // Validate lowercase letters
            var lowerCaseLetters = /[a-z]/g;
            if (myInput.value.match(lowerCaseLetters)) {
                letter.classList.remove("invalid");
                letter.classList.add("valid");
            } else {
                letter.classList.remove("valid");
                letter.classList.add("invalid");
            }

            // Validate capital letters
            var upperCaseLetters = /[A-Z]/g;
            if (myInput.value.match(upperCaseLetters)) {
                capital.classList.remove("invalid");
                capital.classList.add("valid");
            } else {
                capital.classList.remove("valid");
                capital.classList.add("invalid");
            }

            // Validate numbers
            var numbers = /[0-9]/g;
            if (myInput.value.match(numbers)) {
                number.classList.remove("invalid");
                number.classList.add("valid");
            } else {
                number.classList.remove("valid");
                number.classList.add("invalid");
            }

            // Validate length
            if (myInput.value.length >= 10) {
                length.classList.remove("invalid");
                length.classList.add("valid");
            } else {
                length.classList.remove("valid");
                length.classList.add("invalid");
            }
        }
    </script>

</body>

</html>