package data.controller;


import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
		List<NoticeDTO> list=Nservice.NgetList(start, perPage);
		
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
	
	
	@GetMapping("/delete")
	public String delete(String idx,String currentPage,HttpSession session)
	{
		Nservice.NoticeDelete(idx);
		return "redirect:list?currentPage="+currentPage;
	}
	
	@RequestMapping("checkboxdel")
		@ResponseBody
		public void deleteReport(String arrdata) {
      
     
                Nservice.NoticeDelete(arrdata);
            }
        
       
	
	
	@GetMapping("/updateform")
	public ModelAndView updatForm(String idx,String currentPage)
	{
		ModelAndView mview=new ModelAndView();
		NoticeDTO dto=Nservice.getData(idx);
		mview.addObject("dto",dto);
		mview.addObject("currentPage", currentPage);
		mview.setViewName("/notice/updateform");
		return mview;
	}
	
	@PostMapping("/update")
	public String update(@ModelAttribute NoticeDTO dto,
			String currentPage,HttpSession session)
	{
		Nservice.NoticeUpdate(dto);
		
		return "redirect:content?idx="+dto.getIdx()+"&currentPage="+currentPage;
		
		
	}

			
			
	
	

	

	
	@GetMapping("/writeform")
	public String writeform()
	{
		return "/notice/writeform";
	}
	
	
	
	
	@GetMapping("/content")
	public ModelAndView content(@RequestParam String idx,
			@RequestParam(defaultValue="1") int currentPage,
			@RequestParam(required = false) String key
			)
	
	{	
		ModelAndView mview=new ModelAndView();
		if(key!=null)
			Nservice.updateReadCount(idx);
		
		NoticeDTO dto=Nservice.getData(idx);
		
		mview.addObject("dto", dto);
		mview.addObject("currentPage", currentPage);
		mview.setViewName("/notice/content");
		return mview;
	}
	
	
	
	@GetMapping("/update")
	public String update()
	{
		return "/notice/updateform";
	}
	

}
