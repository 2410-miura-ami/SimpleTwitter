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

import chapter6.beans.Message;
import chapter6.logging.InitApplication;
import chapter6.service.MessageService;

@WebServlet(urlPatterns = { "/edit" })
public class EditServlet extends HttpServlet {
	/**
	* ロガーインスタンスの生成
	*/
	Logger log = Logger.getLogger("twitter");

	/**
	* デフォルトコンストラクタ
	* アプリケーションの初期化を実施する。
	*/
	public EditServlet() {
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

		//編集ボタンを押したつぶやきのid(top.jspでパラメータとして渡しているもの)を受け取る
		String messageIdString = request.getParameter("messageId");

		//取得したmessageIdSting(編集画面URLのつぶやきID)が数字ではない時、エラーメッセージの表示
		List<String> errorMessages = new ArrayList<String>();

		//セッションにデータを登録する場合、まず始めにセッションオブジェクトを生成
		HttpSession session = request.getSession();

		if(!messageIdString.matches("^[0-9]+$") || (StringUtils.isBlank(messageIdString))) {
			errorMessages.add("不正なパラメータです");
			//エラーメッセージを格納して、トップ画面へ遷移
			session.setAttribute("errorMessages", errorMessages);
			//リダイレクトで遷移（今回は他のサーバーも通ってる）
			response.sendRedirect("./");
			return;
		}

		//messageIdを数値型に変換
		int messageId = Integer.parseInt(messageIdString);

		//受け取ったmessageIdを使ってDBにて参照していくために、MessageServiceを呼び出す
		//最終的に値をBeansに詰めたいから、型はMessage型
		Message message = new MessageService().select(messageId);

		//つぶやきId存在していないもので処理が進んだら、値なしで返ってくる
		if(message == null) {
			errorMessages.add("不正なパラメータです");
			//エラーメッセージを格納して、トップ画面へ遷移
			session.setAttribute("errorMessages", errorMessages);
			//リダイレクトで遷移（今回は他のサーバーも通ってる）
			response.sendRedirect("./");
			return;
		}

		String messageText =  (String)message.getText();
		int messageIdEdit = (int)message.getId();

		//取得した値をリクエストにセット
		request.setAttribute("messageText", messageText);
		request.setAttribute("messageIdEdit", messageIdEdit);

		//呼び出す画面を指定して、fowardで画面遷移
		request.getRequestDispatcher("edit.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		//更新ボタンを押したつぶやきのidとテキスト(edit.jspでパラメータとして渡しているもの)を受け取る
		String messageIdEditString = request.getParameter("messageId");
		String text = request.getParameter("text");

		//messageIdEditを数値型に変換
		int messageIdEdit = Integer.parseInt(messageIdEditString);

		List<String> errorMessages = new ArrayList<String>();

		if (!isValid(text, errorMessages)) {
			//top.jspでもエラーメッセージの表示を設定しているので、リクエスト領域に格納することで、edit.jspのみで使える値になる。
			request.setAttribute("errorMessages", errorMessages);
			request.setAttribute("messageText", text);
			request.setAttribute("messageId", messageIdEdit);
			request.getRequestDispatcher("edit.jsp").forward(request, response);
			return;
		}

		//受け取ったテキストを使って、DBの更新を行うため、MessageServiceを呼び出す
		new MessageService().update(messageIdEdit, text);

		//最終的にトップ画面へ遷移する
		response.sendRedirect("./");

	}

	//入力値に対するバリデーションを行います。
	private boolean isValid(String text, List<String> errorMessages) {

		log.info(new Object() {
		}.getClass().getEnclosingClass().getName() +
				" : " + new Object() {
				}.getClass().getEnclosingMethod().getName());

		if (StringUtils.isBlank(text)) {
			errorMessages.add("メッセージを入力してください");
		} else if (140 < text.length()) {
			errorMessages.add("140文字以下で入力してください");
		}

		if (errorMessages.size() != 0) {
			return false;
		}
		return true;
	}
}
