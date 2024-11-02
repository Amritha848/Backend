package com.riverside.tamarind.serviceinterfaces;

import java.util.HashMap;
import java.util.List;

import com.riverside.tamarind.dto.TaskDTO;

public interface TaskServiceImplements {
	
     HashMap<?,?> sendTask(TaskDTO dto);
     
     List<TaskDTO> getAllTasks();
     
     void deleteById(String id);

}
