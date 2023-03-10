package com.sw.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.sw.command.BoardService;
import com.sw.command.BoardServiceImpl;
import com.sw.dto.BDto;

/**
 * Servlet implementation class BFrontController
 */
@WebServlet("*.do")
public class BFrontController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BFrontController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		actionDo(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		actionDo(request,response);
	}

		private void actionDo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			System.out.println("actionDo");
			request.setCharacterEncoding("UTF-8");
			
			String uri = request.getRequestURI();
			System.out.println("uri : "+uri);
			
			String conPath = request.getContextPath();
			System.out.println("conPath : "+conPath);
			
			String com = uri.substring(conPath.length());
			System.out.println("command : "+com);
			

			String viewPage = null;
			
			BoardService bCmd = new BoardServiceImpl();
			HttpSession session = request.getSession();
			
			System.out.println("actionDo - "+com);
			
				if(com.equals("/list.do")) {
					System.out.println("-----/list do -----");

					String log = (String)session.getAttribute("Login?");
					System.out.println("???????????? , ????????? ?????? ? =>"+log);
					
					ArrayList<BDto> bList = bCmd.showBoardList(); //????????? ??????
					session.setAttribute("bList", bList);
					
					BoardService tH = new BoardServiceImpl(); // ?????? ????????? ??????
					ArrayList<BDto> Thit = tH.topHit();
					session.setAttribute("bTopHit", Thit);		

					BoardService tG = new BoardServiceImpl(); //?????? ????????? ??????
					ArrayList<BDto> Tgood = tG.topGood();
					session.setAttribute("bTopGood", Tgood);		
					
					
					if(log==null) {
						
						viewPage = "generalForumLogX.jsp";
					}
					else {
						
						String userId = (String)session.getAttribute("ID");
						String bName = bCmd.getUserName(userId);
						
						session.setAttribute("UserName", bName);
						
						
						viewPage = "generalForum.jsp";
					}
				} 
				
	
				
				
				else if(com.equals("/join.do")) {
					
					String bName = request.getParameter("name");
					String bId = request.getParameter("id");
					String bPw = request.getParameter("pw");

					System.out.println("???????????? ??????");
					System.out.println("bName =>"+bName);
					System.out.println("bId =>"+bId);
					
					
					if(bName.equals("") || bId.equals("")) {
						alertAndGo(response,"???????????? ????????? ???????????? ??? ??? ????????????.","register.jsp");
					}
					
					else {
						int result = bCmd.registerCheck(bId);
						
						if(result==1) {
							alertAndGo(response,"??????????????? ?????????????????????.","login.jsp");
							bCmd.register(bName, bId, bPw);
						}
						else {
							
							System.out.println("???");
							alertAndGo(response,"????????? ????????? ?????????.","register.jsp");
						}
					}
		
					
				}
				
				else if(com.equals("/login_view.do")) {
					viewPage = "login.jsp";
				}
				
				else if(com.equals("/login.do")) {
					String bId = request.getParameter("id");
					String bPw = request.getParameter("pw");
					
					int result = bCmd.login(bId, bPw);
					
					if(result == 0) {
						String msg = "???????????? ?????????????????????.";
						alertAndGo(response,msg,"login.jsp");
						
						viewPage = "login.jsp"; 
					}
					
					else {
						session.setAttribute("ID", bId); //???????????? ????????? ??????
						session.setAttribute("Login?", "yes"); //????????? ????????? ???.
						
						viewPage ="list.do";  
					}
					
					
				} 
				
				else if(com.equals("/logout.do")) {
					
					session.invalidate();
					
					alertAndGo(response,"???????????? ?????????.","list.do");
				} 
				
				else if(com.equals("/write_view.do")) {
					
					String log = (String)session.getAttribute("Login?");
					
					System.out.println(log);
					
					if(log==null) {
						alertAndGo(response,"?????? ?????????????????? ???????????? ??????????????????.","generalForumLogX.jsp");
					}
					else{
						viewPage = "createForm.jsp";
					}
				} 
				
				else if(com.equals("/write.do")) {
					
					String bTitle =request.getParameter("input-title");
					String bDetail =request.getParameter("input-content");
					String userId = (String)session.getAttribute("ID");
					
					
					
					if(userId==null) System.out.println("Session s getAttr is NULL !!!");

					String bName = bCmd.getUserName(userId);
					
					System.out.println(bTitle);
					System.out.println(bDetail);
					System.out.println(userId);
					System.out.println(bName);
					
					BDto bdto= new BDto(userId,bTitle, bName,0,0,bDetail);
					
					bCmd.writeContent(bdto);
					
					alertAndGo(response,"???????????? ??????????????? ?????????????????????.","list.do");
					
				}
				
				else if(com.equals("/content_view.do")) {
					
					//??? ????????? ?????? ????????? ????????????
					System.out.println("??? ????????? ?????? ????????? ???????????? ?????????");
					String UserId=(String)session.getAttribute("ID");
					
					System.out.println("???????????? ????????? ????????? =>"+UserId);

					System.out.println("\n????????? ?????? ??????.");

					String sNum = request.getParameter("bId");
					int bNum = Integer.parseInt(sNum);
					
					System.out.println("????????? ?????? sNum =>"+bNum);
					
					
					BDto bdto = bCmd.viewContent(bNum);
					
					//????????? ????????? ?????????????????????.
					
					String dbId=bdto.UserId;
					System.out.println("db?????? ????????? ???????????? id =>"+dbId);
					

					
					session.setAttribute("bdto",bdto);
					
					if(dbId.equals(UserId)) {
						viewPage = "browseMyPost.jsp";
					}
					else if(UserId==null) {
						viewPage = "browsePost.jsp";
					}
					else {
						viewPage = "browsePost.jsp";
					}

					System.out.println("\n????????? ?????? ????????????.");
				}
				
				else if(com.equals("/modify_view.do")) {
					
					String sId = request.getParameter("bId"); //????????? ????????? ????????????.
					int bId=Integer.parseInt(sId);
					
					BDto bdto = bCmd.viewContent(bId); //bId ??? ????????? ??????
					
					session.setAttribute("beforemodi",bdto);
					
					viewPage = "modifyForm.jsp";
				}
				
				else if(com.equals("/modify.do")) {
					
					String bTitle =request.getParameter("input-title");
					String bDetail =request.getParameter("input-content");
					String sNum=request.getParameter("bNum");
							
					int bNum=Integer.parseInt(sNum);
					
					
					
					BDto bdto= new BDto(null,bNum,bTitle,null,0,0,bDetail);	
					
					bCmd.modifyContent(bdto);
					
					alertAndGo(response,"????????? ????????? ?????????????????????.","list.do");
				}
				
				else if(com.equals("/good.do")) {

					System.out.println("?????? ??????");
					String sId = request.getParameter("bId"); //????????? ????????? ????????????.
					int bId=Integer.parseInt(sId);

				
					String Id = (String)session.getAttribute("ID");
					
					if(Id==null) {
						alertAndGo(response,"????????? ??? ?????? ????????? ???????????????..","javascript:window.history.back()");
					}
					else {
						
						bCmd.good(bId);
						alertAndBack(response,"??? ???????????? ???????????????.");
						
					}
					
				}
				
				else if(com.equals("/delete.do")) {
					String sId = request.getParameter("bId"); //????????? ????????? ????????????.

					int bId=Integer.parseInt(sId);
					
					bCmd.deleteContent(bId);
					viewPage = "list.do";
					
					alertAndGo(response,"???????????? ??????????????? ?????????????????????.","list.do");

				}
				
				response.sendRedirect(viewPage);
			
		}

		public static void alertAndGo(HttpServletResponse response, String msg, String url) {
		    try {
		        response.setContentType("text/html; charset=utf-8");
		        PrintWriter w = response.getWriter();
		        w.write("<script>alert('"+msg+"');location.href='"+url+"';</script>");
		        w.flush();
		        w.close();
		    	} catch(Exception e) {
		        e.printStackTrace();
		    }
		    
		    
		}
		
		
		public static void alertAndBack(HttpServletResponse response, String msg) {
		    try {
		        response.setContentType("text/html; charset=utf-8");
		        PrintWriter w = response.getWriter();
		        w.write("<script>alert('"+msg+"');history.go(-1);</script>");
		        w.flush();
		        w.close();
		    } catch(Exception e) {
		        e.printStackTrace();
		    }
		}
	
}
