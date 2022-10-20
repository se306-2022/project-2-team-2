```python
fun recurse():
  if freeTasks is empty:
    fastestTime = currentFastestTime
  
  for freeTask in freeTasks:
    deqeue freeTask from freeTasks
    
    for processor in processors:
      schedule freeTask on processor
      
      if cost(freeTask, processor) >= fastestTime
        recurse()
      
      remove freeTask on processor
      
    enqueue freeTask
```
