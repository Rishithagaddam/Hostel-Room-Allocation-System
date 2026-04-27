<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hostel Allocation - Login</title>
    <link rel="stylesheet" href="/hostel-allocation/css/style.css">
    <style>
        .login-card .password-input-wrapper {
            position: relative;
            display: flex;
            align-items: center;
            width: 100%;
        }

        .login-card .password-input-wrapper #password {
            width: 100%;
            padding-right: 44px;
            margin: 0;
        }

        .login-card .password-input-wrapper .password-toggle {
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            width: 24px;
            height: 24px;
            padding: 0;
            margin: 0;
            border: none;
            background: transparent;
            color: #6c757d;
            cursor: pointer;
            font-size: 16px;
            line-height: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 2;
        }

        .login-card .password-input-wrapper .password-toggle:hover,
        .login-card .password-input-wrapper .password-toggle.visible,
        .login-card .password-input-wrapper .password-toggle:focus {
            color: #4A6FA5;
            outline: none;
        }
    </style>
</head>
<body class="login-page">
    <div class="login-container">
        <div class="login-card">
            <div class="login-header">
                <h1>🏨 Hostel Allocation System</h1>
                <p>Student Room Management Portal</p>
            </div>

            <form method="POST" action="/hostel-allocation/login" class="login-form">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" required placeholder="Enter your username">
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <div class="password-input-wrapper">
                        <input type="password" id="password" name="password" required placeholder="Enter your password">
                        <button type="button" class="password-toggle" id="passwordToggle" aria-label="Show password" title="Show password">&#128065;</button>
                    </div>
                </div>

                <div class="form-group">
                    <label for="role">Login As</label>
                    <select id="role" name="role" required>
                        <option value="">-- Select Role --</option>
                        <option value="warden">Warden (Admin)</option>
                        <option value="student">Student</option>
                    </select>
                </div>

                <button type="submit" class="btn btn-primary">Login</button>
            </form>
        </div>
    </div>

    <script>
        (function () {
            const passwordInput = document.getElementById('password');
            const toggleButton = document.getElementById('passwordToggle');

            if (!passwordInput || !toggleButton) {
                return;
            }

            toggleButton.addEventListener('click', function () {
                const isPassword = passwordInput.type === 'password';
                passwordInput.type = isPassword ? 'text' : 'password';
                toggleButton.classList.toggle('visible', isPassword);
                toggleButton.setAttribute('aria-label', isPassword ? 'Hide password' : 'Show password');
                toggleButton.setAttribute('title', isPassword ? 'Hide password' : 'Show password');
            });
        })();
    </script>
</body>
</html>
