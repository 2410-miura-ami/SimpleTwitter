package chapter6.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(urlPatterns = { "/setting", "/edit" })
public class LoginFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		//セッションのオブジェクト用意
		HttpSession session = httpRequest.getSession();
		String loginUser = (String) session.getAttribute("loginUser");

		//セッション領域にloginUserの情報があるかどうかで、ログインしているか判断する
		//loginUserが空っぽなら、ログインしていない→ログインページに飛ぶ

		if (loginUser != null) {
			// loginUserがNULLでなければ、通常どおりの遷移
			//サーブレットを実行
			chain.doFilter(request, response);
		} else {
			// セッションがNullならば、ログイン画面へ飛ばす
			List<String> errorMessages = new ArrayList<String>();
			errorMessages.add("ログインしてください");
			session.setAttribute("errorMessages", errorMessages);
			httpResponse.sendRedirect("login.jsp");
			return;
		}

	}

	@Override
	public void init(FilterConfig config) {
	}

	@Override
	public void destroy() {
	}

}
