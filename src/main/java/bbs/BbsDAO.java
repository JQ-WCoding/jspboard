package bbs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

//DAO Data Access Object
// 데이타 접근 객체
public class BbsDAO {
	// connection :
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;

	// 기본 생성자
	public BbsDAO() {
		// 예외사항이 발생하기에 try catch문을 사용해야 connect가능
		try {
			String dbURL = "jdbc:mysql://localhost:3306/BBS";
			String dbID = "root";
			String dbPassword = "root";
			// Driver 클래스 로딩시 객체 생성 / DriverManager에 해당 url과 MySQL에 등록된 아이디 비밀번호를 통해 연결상태 유지
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(dbURL, dbID, dbPassword);
		} catch (Exception e) { // 찾지 못할경우 ClassNotFoundException 발생 가능성 있음
			// 해당 오류 추적 메소드
			e.printStackTrace();
		}
	}

	/**
	 * 글 날짜 가져오는 메소드 sql쿼리를 통해 쿼리 내 now() 함수로 현재 시간(날짜) 가져오기
	 * 
	 * @return
	 */
	public String getDate() {
		// select 구문의 사용에서 executequery 를 사용
		String SQL = "SELECT NOW()";
		try {
			// connection 의 pstmt (준비단계) 메소드에 sql쿼리 문을 매개변수로 적용
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			// result 현재 결과를 어떠한 형태로 받아올 것인지 select와 update문구에 따라 달라진다
			rs = pstmt.executeQuery();
			// 결과값에 다음 줄이 존재 한다면
			if (rs.next()) {
				// String형 리턴값으로 첫번째 행의 값을 가져온다
				return rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 오류가 날 경우 String null상태
		return "";
	}

	/**
	 * 다음 글 순번 확인 메소드 새로 글을 등록할때 몇번째 글인지 정하기 위해 이전의 최신글 확인
	 * 
	 * @return
	 */
	public int getNext() {
		// select 구문으로 bbs 테이블에서 bbsid 컬럼을 내림차순하여 정렬한 후 bbsid값들을 가져온다
		String SQL = "SELECT bbsID FROM BBS ORDER BY bbsID DESC";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			// 만약 bbsid 즉 게시글이 하나라도 존재한다면
			if (rs.next()) {
				// 해당 게시글 순번 +1 을 해준다
				return rs.getInt(1) + 1;
			}
			// 게시글이 하나도 없다면 기본적으로 1번부터 시작한다
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 게시글 작성 메소드
	 * 
	 * @param bbsTitle
	 * @param userID
	 * @param bbsContent
	 * @return
	 */
	public int write(String bbsTitle, String userID, String bbsContent) {
		// INSERT문을 통해 BBS 테이블 내에 VALUES() 내의 값을 순차적으로 주입
		String SQL = "INSERT INTO BBS VALUES(?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			// index 1 -> 상단의 메소드를 통해 다음 게시글의 번호를 주입
			// setInt 형으로 int형으로 값을 주입할 예정이라는 표현
			pstmt.setInt(1, getNext()); // 게시글 번호
			// String형으로 값 주입 예정
			pstmt.setString(2, bbsTitle); // 게시글 제목
			pstmt.setString(3, userID); // 작성자
			pstmt.setString(4, getDate()); // getDate메소드를 사용
			pstmt.setString(5, bbsContent); // 본문 확인
			pstmt.setInt(6, 1); // bbsAvailable로 기본값을 1로 설정 (글이 처음 등록될땐 변경 여부에 대한 이유가 필요없다)
			// executeupdate 메소드를 이용해 sql에 연동 return으로 1이 반환된다 (성공시)
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 해당 메소드에 완료되지 않는다면 -1이 반환되어 실패 여부 확인 가능
		return -1;
	}

	/**
	 * 
	 * @param pageNumber
	 * @return
	 */
	public ArrayList<Bbs> getList(int pageNumber) {
		String SQL = "SELECT * FROM bbs WHERE bbsID < ? AND bbsAvailable = 1 ORDER BY bbsID DESC LIMIT 10";
		ArrayList<Bbs> list = new ArrayList<Bbs>();

		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Bbs bbs = new Bbs();
				bbs.setBbsID(rs.getInt(1));
				bbs.setBbsTitle(rs.getString(2));
				bbs.setUserID(rs.getString(3));
				bbs.setBbsDate(rs.getString(4));
				bbs.setBbsContent(rs.getString(5));
				bbs.setBbsAvailable(rs.getInt(6));
				list.add(bbs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	// ����¡ ó�� ��
	public boolean nextPage(int pageNumber) {
		String SQL = "SELECT * FROM bbs WHERE bbsID < ? AND bbsAvailable = 1";
//		ArrayList<Bbs> list = new ArrayList<Bbs>();
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public Bbs getBbs(int bbsID) {
		String SQL = "SELECT * FROM bbs WHERE bbsID = ?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, bbsID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				Bbs bbs = new Bbs();
				bbs.setBbsID(rs.getInt(1));
				bbs.setBbsTitle(rs.getString(2));
				bbs.setUserID(rs.getString(3));
				bbs.setBbsDate(rs.getString(4));
				bbs.setBbsContent(rs.getString(5));
				bbs.setBbsAvailable(rs.getInt(6));
				return bbs;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int update(int bbsID, String bbsTitle, String bbsContent) {
		String SQL = "UPDATE bbs SET bbsTitle = ?, bbsContent = ? WHERE bbsID= ?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, bbsTitle);
			pstmt.setString(2, bbsContent);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int delete(int bbsID) {
		String SQL = "UPDATE bbs SET bbsAvailable = 0 WEHRE bbsID = ?";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, bbsID);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
}
