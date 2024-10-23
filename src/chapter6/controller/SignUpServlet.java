package chapter6.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.User;
import chapter6.logging.InitApplication;
import chapter6.service.UserService;

@WebServlet(urlPatterns = { "/signup" })
public class SignUpServlet extends HttpServlet {
	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public SignUpServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	@Override
	//get呼び出しされると実行され、signup.jspを表示します。
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		//以下の処理request.getRequestDispatcher～
		//①request.getRequestDispatcherメソッドの引数に遷移する画面を指定
		//②forwardメソッドを呼び出すと、遷移が行われる

		request.getRequestDispatcher("signup.jsp").forward(request, response);
	}

	@Override
	//post呼び出しされると実行。リクエストパラメータを Userオブジェクトにセットし、
	//Serviceのメソッドを呼び出してDBへユーザーの登録を行います。
	//ユーザーの登録が完了したら再びトップ画面を表示するようになっています。
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		List<String> errorMessages = new ArrayList<String>();

		User user = getUser(request);
		if (!isValid(user, errorMessages)) {
			request.setAttribute("errorMessages", errorMessages);
			request.getRequestDispatcher("signup.jsp").forward(request, response);
			return;
		}
		new UserService().insert(user);
		//sendRedirectの引数にURLを指定することでリダイレクトが行われます。
		//これが実行されると、TopServletのdoGetが呼び出され→top.jspが呼び出されている
		response.sendRedirect("./");
	}

	//ユーザー登録画面（signup.jsp ※後述）からの入力値（リクエストパラメータ）を取得
	private User getUser(HttpServletRequest request) throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		//入力された値の取得
		//Servletでは、signup.jspで<input>タグのname属性に付けた名前を
		//request.getParameterの引数に同じ文字列を指定することで値が取得できます
		User user = new User();
		user.setName(request.getParameter("name"));
		user.setAccount(request.getParameter("account"));
		user.setPassword(request.getParameter("password"));
		user.setEmail(request.getParameter("email"));
		user.setDescription(request.getParameter("description"));
		return user;
	}

	//入力値に対するバリデーションを行います。
	//入力値が不正な場合には再度、自画面(signup)を表示するようにしています。
	private boolean isValid(User user, List<String> errorMessages) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		String name = user.getName();
		String account = user.getAccount();
		String password = user.getPassword();
		String email = user.getEmail();

		if (!StringUtils.isEmpty(name) && (20 < name.length())) {
			errorMessages.add("名前は20文字以下で入力してください");
		}

		if (StringUtils.isEmpty(account)) {
			errorMessages.add("アカウント名を入力してください");
		} else if (20 < account.length()) {
			errorMessages.add("アカウント名は20文字以下で入力してください");
		}

		if (StringUtils.isEmpty(password)) {
			errorMessages.add("パスワードを入力してください");
		}

		if (!StringUtils.isEmpty(email) && (50 < email.length())) {
			errorMessages.add("メールアドレスは50文字以下で入力してください");
		}

		User userAccount = new UserService().select(account);
		if(userAccount != null) {
			errorMessages.add("すでに存在するアカウントです");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}

}
