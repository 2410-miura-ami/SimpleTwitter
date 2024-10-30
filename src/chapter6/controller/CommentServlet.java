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

import chapter6.beans.Comment;
import chapter6.beans.User;
import chapter6.logging.InitApplication;
import chapter6.service.CommentService;

@WebServlet(urlPatterns = { "/comment" })
public class CommentServlet extends HttpServlet {
	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public CommentServlet() {
		InitApplication application = InitApplication.getInstance();
		application.init();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		//返信ボタンを押下した時の、パラメータcommentId・commentTextを受け取る
		String messageIdString = request.getParameter("messageId");
		String commentText = request.getParameter("commentText");

		//commentIdを数値型に変換する
		int messageId = Integer.parseInt(messageIdString);

		//セッションにデータを登録する場合、まず始めにセッションオブジェクトを生成
		HttpSession session = request.getSession();
		List<String> errorMessages = new ArrayList<String>();

		if (!isValid(commentText, errorMessages)) {
			session.setAttribute("errorMessages", errorMessages);
			response.sendRedirect("./");
			return;
		}

		//commentTextとcommentIdをBeansにセットし直している
		Comment comment = new Comment();
		comment.setText(commentText);
		comment.setMessageId(messageId);


		User user = (User) session.getAttribute("loginUser");
		comment.setUserId(user.getId());

		//受け取ったパラメータを使って、DBの登録を行うため、insertメソッドでCommentServiceを呼び出す
		new CommentService().insert(comment);

		//トップへ遷移
		response.sendRedirect("./");
	}

	//入力値に対するバリデーションを行います。
	//入力値が不正な場合にはトップ画面(top.jsp)を表示するようにしています。
	private boolean isValid(String commentText, List<String> errorMessages) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		if (StringUtils.isBlank(commentText)) {
			errorMessages.add("メッセージを入力してください");
		} else if (140 < commentText.length()) {
			errorMessages.add("140文字以下で入力してください");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}

}
