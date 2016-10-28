<!doctype html>
<html lang="us">
<head>
<!--Jquery UI-->
<link rel="stylesheet"
	href="${context_path}/static/js/vendor/jquery-ui/jquery-ui.css">
<!--JQuery-->
<script type="text/javascript"
	src="${context_path}/static/js/vendor/jquery-1.11.0.min.js"></script>
<!-- Bootstrap JS-->
<script type="text/javascript"
	src="${context_path}/static/js/vendor/bootstrap.min.js"></script>
<!-- DateTimePicker -->
<script type="text/javascript"
	src="${context_path}/static/js/vendor/datetimepicker.js"></script>
<!--JQuery UI-->
<script type="text/javascript"
	src="${context_path}/static/js/vendor/jquery-ui/jquery-ui.min.js"></script>
<!-- CSS Files -->
<link href="${context_path}/static/css/bootstrap.min.css" rel="stylesheet" media="screen">
<link href="${context_path}/static/css/font-awesome.min.css" rel="stylesheet">
<link href="${context_path}/static/fonts/icon-7-stroke/css/pe-icon-7-stroke.css"
	rel="stylesheet">
<link href="${context_path}/static/css/animate.css" rel="stylesheet" media="screen">
<link href="${context_path}/static/css/owl.theme.css" rel="stylesheet">
<link href="${context_path}/static/css/owl.carousel.css" rel="stylesheet">

<!-- Colors -->
<link href="${context_path}/static/css/css-index.css" rel="stylesheet" media="screen">

<!-- Google Fonts -->
<link rel="stylesheet"
	href="http://fonts.googleapis.com/css?family=Lato:100,300,400,700,900,100italic,300italic,400italic,700italic,900italic" />
<!-- /.website title -->

<title>Sample Project</title>
<style>
.error-container {
	word-wrap: break-word;
}
</style>
</head>

<body data-spy="scroll" data-target="#navbar-scroll">

	<!-- /.preloader -->
	<div id="preloader"></div>
	<div id="top"></div>

	<!-- /.parallax full screen background image -->
	<div class="fullscreen landing parallax"
		style="background-image: url('${context_path}/static/images/coast.jpg');"
		data-img-width="2000" data-img-height="1333" data-diff="100">

		<div class="overlay">
			<div class="container">
				<div class="row">
					<div class="col-md-12">

						<!-- /.logo -->
						<div class="logo wow fadeInDown">
							<a href=""><h1 style="color: orange">Sample Project</h1></a>
						</div>

						<!-- /.main title -->
						<h1 class="wow fadeInLeft">Welcome To The Sample Project</h1>

						<!-- /.header paragraph -->
						<div class="landing-text wow fadeInUp">
							<p>Sample Project is a modern and customizable landing page
								template designed to increase conversion of your product. Sample
								Project is flexible to suit any kind of business.</p>
						</div>

					</div>

					<!-- /.login form -->
					<div class="col-md-3"></div>
					<div class="col-md-6">
						<div class="signup-header wow fadeInUp">
							<h3 class="form-title text-center">LOGIN</h3>
							<form class="form-header" id="loginForm" action="login" method="post" role="form">
								<#if RequestParameters.error??>
									<div class="alert alert-danger alert-dismissible" role="alert">
										<button type="button" class="close" data-dismiss="alert">
											<span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
										</button>
										<div class="text-center error-container">
											<#if SPRING_SECURITY_LAST_EXCEPTION??> <span>${SPRING_SECURITY_LAST_EXCEPTION.message}</span>
											<#else> <span>There has been an error with authentication</span>
											</#if>
										</div>
									</div>
								</#if>
								<div class="form-group">
									<input id="username" name="username" type="text" class="form-control input-lg" placeholder="Enter username" required> 
									<span id="usernameError" class="errorText" style="display: none">This is a required field.</span>
								</div>
								<div class="form-group">
									<span id="passwordCapsLock" class="capsLockText" style="display: none">Caps lock is on</span> 
									<input id="password" name="password" type="password" class="form-control js-password input-lg" placeholder="Enter password" required>
									<span id="passwordError" class="errorText" style="display: none">This is required field.</span>
								</div>
								<div class="form-group last">
								<input type="submit" id="submitButton" name="submit" class=";btn btn-warning btn-block btn-lg" value="LOGIN">
								</div>
								<p class="privacy text-center">
									We will not share your email. Read our <a href="privacy.html">privacy policy</a>.
								</p>
							</form>
						</div>
					</div>
					<div class="col-md-3"></div>
				</div>
			</div>
		</div>
	</div>
</body>
<!-- /.javascript files -->
    <script src="${context_path}/static/js/jquery.js"></script>
    <script src="${context_path}/static/js/bootstrap.min.js"></script>
    <script src="${context_path}/static/js/custom.js"></script>
    <script src="${context_path}/static/js/jquery.sticky.js"></script>
	<script src="${context_path}/static/js/wow.min.js"></script>
	<script src="${context_path}/static/js/owl.carousel.min.js"></script>
	<script>
		new WOW().init();
		
		$(function() {
			$('#submitButton').click(function() {
				var username = $('#username');
				var password = $('#password');
				var invalidFilters = 0;

				//validate the username field
				if (username.val() === '') {
					username.addClass('errorBorder');
					$('#usernameError').show();
					invalidFilters++;
				} else {
					username.removeClass('errorBorder');
					$('#usernameError').hide();
				}

				//validate the password field
				if (password.val() === '') {
					$('.js-password').addClass('errorBorder');
					$('#passwordError').show();
					invalidFilters++;
				} else {
					$('.js-password').addClass('errorBorder');
					$('#passwordError').hide();
				}

				//if no errors submit
				if (invalidFilters === 0) {
					$('#loginForm').submit();
				}
			});

			//show and hide the caps lock warnings
			$('#password').keypress(function(event) {
				showHideCapsLockError(event, $('#passwordCapsLock'));
			});

			$(document).keypress(function(event) {
				if (event.keyCode == 13) {
					$('#submitButton').click();
					event.preventDefault();
				}
			});
		});
	</script>

</html>