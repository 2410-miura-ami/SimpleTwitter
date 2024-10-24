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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;

import chapter6.beans.User;
import chapter6.exception.NoRowsUpdatedRuntimeException;
import chapter6.logging.InitApplication;
import chapter6.service.UserService;

@WebServlet(urlPatterns = { "/setting" })

public class SettingServlet extends HttpServlet {
	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public SettingServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		//セッションにデータを登録する場合、まず始めにセッションオブジェクトを生成。セッションが利用できるようになる
		HttpSession session = request.getSession();
		//LoginServletでセッションに"loginUser"という名前でセットした値を受け取る
		//登録されたデータはObject型で取得されるため、取得されたデータを適切に扱うためには、型をセッションへ登録される前の型に明示的に変換
		User loginUser = (User) session.getAttribute("loginUser");

		User user = new UserService().select(loginUser.getId());

		//リクエストに「user」という名前で上記の「user」をセット
		request.setAttribute("user", user);
		//呼び出す画面を指定して、fowardで画面遷移
		request.getRequestDispatcher("setting.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		//セッションにデータを登録する場合、まず始めにセッションオブジェクトを生成
		HttpSession session = request.getSession();

		List<String> errorMessages = new ArrayList<String>();

		User user = getUser(request);
		if (isValid(user, errorMessages)) {
			try {
				new UserService().update(user);
			} catch (NoRowsUpdatedRuntimeException e) {
				log.warning("他の人によって更新されています。最新のデータを表示しました。データを確認してください。");
				errorMessages.add("他の人によって更新されています。最新のデータを表示しました。データを確認してください。");
			}
		}

		if (errorMessages.size() != 0) {
			//リクエストへ値のセット
			request.setAttribute("errorMessages", errorMessages);
			request.setAttribute("user", user);
			//呼び出す画面を指定して、fowardで画面遷移
			request.getRequestDispatcher("setting.jsp").forward(request, response);
			return;
		}

		//セッションへ値のセット
		session.setAttribute("loginUser", user);
		response.sendRedirect("./");
	}

	private User getUser(HttpServletRequest request) throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		User user = new User();
		user.setId(Integer.parseInt(request.getParameter("id")));
		user.setName(request.getParameter("name"));
		user.setAccount(request.getParameter("account"));
		user.setPassword(request.getParameter("password"));
		user.setEmail(request.getParameter("email"));
		user.setDescription(request.getParameter("description"));
		return user;
	}

	private boolean isValid(User user, List<String> errorMessages) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		String name = user.getName();
		String account = user.getAccount();
		String email = user.getEmail();
		int id = user.getId();

		if (!StringUtils.isEmpty(name) && (20 < name.length())) {
			errorMessages.add("名前は20文字以下で入力してください");
		}
		if (StringUtils.isEmpty(account)) {
			errorMessages.add("アカウント名を入力してください");
		} else if (20 < account.length()) {
			errorMessages.add("アカウント名は20文字以下で入力してください");
		}
		if (!StringUtils.isEmpty(email) && (50 < email.length())) {
			errorMessages.add("メールアドレスは50文字以下で入力してください");
		}

		User duplicationAccount  = new UserService().select(account);
		if((duplicationAccount != null) && (duplicationAccount.getId() != id)) { //idの比較で自分自身ではない、かつ、userAccountがnullではない時の条件
			errorMessages.add("すでに存在するアカウントです");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}

}
