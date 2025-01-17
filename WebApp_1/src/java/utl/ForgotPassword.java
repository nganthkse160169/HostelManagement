package utl;

import dao.UserDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class ForgotPassword
 */
@WebServlet("/forgotPassword")
public class ForgotPassword extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String email = request.getParameter("email");

        RequestDispatcher dispatcher = null;
        int otpvalue = 0;
        HttpSession mySession = request.getSession();
        UserDAO dao = new UserDAO();
        try {
            boolean checkEmail = dao.checkEmailDuplicate(email);
            if (checkEmail) {
                if (email != null || !email.equals("")) {
                    // sending otp
                    Random rand = new Random();
                    otpvalue = rand.nextInt(1255650);

                    String to = email;// change accordingly
                    // Get the session object
                    Properties props = new Properties();
                    props.put("mail.smtp.host", "smtp.gmail.com");
                    props.put("mail.smtp.socketFactory.port", "465");
                    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.port", "465");
                    Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication("trungntse151136@fpt.edu.vn", "nftyiamneihipeqt");// Put your email
                            // id and
                            // password here
                        }
                    });
                    // compose message
                    try {
                        MimeMessage message = new MimeMessage(session);
                        message.setFrom(new InternetAddress(email));// change accordingly
                        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                        message.setSubject("Hello");
                        message.setText("Your OTP is: " + otpvalue + ". Please do not reveal this OTP to anyone");
                        // send message
                        Transport.send(message);
                        System.out.println("message sent successfully");
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                    dispatcher = request.getRequestDispatcher("enterOtp.jsp");
                    request.setAttribute("message", "OTP is sent to your email :" + email);
                    //request.setAttribute("connection", con);
                    mySession.setAttribute("otp", otpvalue);
                    mySession.setAttribute("email", email);
                    dispatcher.forward(request, response);
                    //request.setAttribute("status", "success");
                }
            }else{
                dispatcher = request.getRequestDispatcher("forgot_password.jsp");
                request.setAttribute("ERROR", "Email is not exist.");
                dispatcher.forward(request, response);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ForgotPassword.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
