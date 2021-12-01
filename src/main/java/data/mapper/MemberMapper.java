package data.mapper;

import java.util.HashMap;
import org.apache.ibatis.annotations.Mapper;
import data.dto.MemberDTO;

@Mapper
public interface MemberMapper {
	public void insertMember(MemberDTO dto);
	public int getIdCheck(String id);
	public int getNickCheck(String nick);
	public String getPw(String id);
	public MemberDTO getMemberId(String id);
	public String getIdFindMember(HashMap<String, String> map);
	public String currentUserNickName(String userId);
}
