package chapter6.controller;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import chapter6.beans.User;
import chapter6.beans.UserComment;
import chapter6.beans.UserMessage;
import chapter6.logging.InitApplication;
import chapter6.service.CommentService;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/index.jsp" })

public class TopServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	/**
	    * ロガーインスタンスの生成。ログが出力できるようにするための実装
	    */
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public TopServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		//セッションからログインユーザーを取得し、ログインユーザーのオブジェクトが取得できた場合(nullではなかった場合)には
		//変数isShowMessageFormにtrueを設定するというコード
		boolean isShowMessageForm = false;
		User user = (User) request.getSession().getAttribute("loginUser");
		if (user != null) {
			isShowMessageForm = true;
		}

		//つぶやきの絞り込み。パラメータ取得
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");

		/*
		 * String型のuser_idの値をrequest.getParameter("user_id")で
		 * JSPから受け取るように設定
		 * MessageServiceのselectに引数としてString型のuser_idを追加
		 */
		String userId = request.getParameter("user_id");
		List<UserMessage> messages = new MessageService().select(userId, startDate, endDate);

		List<UserComment> comments = new CommentService().select();
		//リクエストにcommentsを格納
		request.setAttribute("comments", comments);

		//リクエストスコープに"messages"というkeyで、"messages"というvalueを設定。
		request.setAttribute("messages", messages);
		request.setAttribute("isShowMessageForm", isShowMessageForm);
		request.setAttribute("startDate", startDate);
		request.setAttribute("endDate", endDate);

		request.getRequestDispatcher("/top.jsp").forward(request, response);
	}
}
