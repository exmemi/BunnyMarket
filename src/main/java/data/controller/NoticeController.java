package data.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import data.dto.NoticeDTO;
import data.mapper.NoticeMapper;
import data.service.NoticeService;

@Controller
@RequestMapping("/notice")
public class NoticeController {
	
	
	@Autowired
	NoticeService Nservice;
	
	@Autowired
	NoticeMapper Nmapper;
	
	
	
	@GetMapping("/list")
	public ModelAndView notice(
			@RequestParam(defaultValue ="1") int currentPage//������������ȣ
			
			)

	{	
		ModelAndView mview=new ModelAndView();
		
		int totalCount=Nservice.NgetTotalCount();
		
		
		int perPage=5; //�� �������� ������ ���� ����
		int totalPage; // �� ������ ��
		int start; // �� ���������� �ҷ��� db���۹�ȣ
		int perBlock=5; // ��� ���������� ǥ���� ���ΰ�
		int startPage; //�� ���� ǥ���� ����������
		int endPage; //�� ���� ǥ���� ������ ������
		
		//�� ������ ���� ���ϱ�
		totalPage=totalCount/perPage+(totalCount%perPage==0?0:1);
		//�� ���� ����������
		startPage=(currentPage-1)/perBlock*perBlock+1;
		endPage=startPage+perBlock-1;
		if(endPage>totalPage)
			endPage=totalPage;
		//�� ���������� �ҷ��� ���۹�ȣ
		start=(currentPage-1)*perPage;
		//�� ���������� �ʿ��� �Խñ� ��������
		List<NoticeDTO> list=Nservice.NgetList(startPage, perPage);
		
		int no=totalCount-(currentPage-1)*perPage;
		
		mview.addObject("list", list);
		mview.addObject("startPage", startPage);
		mview.addObject("endPage", endPage);
		mview.addObject("totalPage", totalPage);
		mview.addObject("no", no);
		mview.addObject("currentPage", currentPage);
		mview.addObject("totalCount",totalCount);
		mview.setViewName("/notice/list");
		return mview;
		
		
		
		
	}
	
	@PostMapping("insert")
	public String insert(@ModelAttribute NoticeDTO dto,HttpSession session)
	{
		
		
		//insert
		Nservice.NoticeInsert(dto);
		return "redirect:content?idx="+Nservice.getMaxidx();
		
		
	
		
	}
	

	

	
	@GetMapping("/writeform")
	public String writeform()
	{
		return "/notice/writeform";
	}
	
	@GetMapping("/content")
	public String content()
	{
		return "/notice/content";
	}
	
	@GetMapping("/update")
	public String update()
	{
		return "/notice/updateform";
	}
	

}
