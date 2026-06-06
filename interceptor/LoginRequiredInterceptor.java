package tw.edu.fju.miniclinic.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;




import jakarta.servlet.http.HttpSession;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object loggedIn = session.getAttribute("loggedInDoctorId");

        if (loggedIn == null) {
            String path = request.getRequestURI();
            if (path.startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"請先登入\"}");
            } else {
                response.sendRedirect("/login");
            }
            return false;
        }

        return true;
    }
}