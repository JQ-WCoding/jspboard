package bbs;
//practice

// Bbs 게시판 관련 데이터들을 캡슐화하는 공간
public class Bbs {
	// 게시판 넘버링
	private int bbsID;
	// 게시판 제목
	private String bbsTitle;
	// 게시판 작성자 아이디
	private String userID;
	// 해당 글 등록 날짜
	private String bbsDate;
	// 해당 글 본문
	private String bbsContent;
	// 해당 글의 삭제 여부 확인
	private int bbsAvailable;

	public int getBbsID() {
		return bbsID;
	}

	public void setBbsID(int bbsID) {
		this.bbsID = bbsID;
	}

	public String getBbsTitle() {
		return bbsTitle;
	}

	public void setBbsTitle(String bbsTitle) {
		this.bbsTitle = bbsTitle;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getBbsDate() {
		return bbsDate;
	}

	public void setBbsDate(String bbsDate) {
		this.bbsDate = bbsDate;
	}

	public String getBbsContent() {
		return bbsContent;
	}

	public void setBbsContent(String bbsContent) {
		this.bbsContent = bbsContent;
	}

	public int getBbsAvailable() {
		return bbsAvailable;
	}

	public void setBbsAvailable(int bbsAvailable) {
		this.bbsAvailable = bbsAvailable;
	}

}
