import request from '@/utils/request'

export function getList(params) {
  return request({
    url: '/task/pagelist',
    method: 'get',
    params
  })
}

// 新增任务
export function save(params) {
  return request({
    url: '/task',
    method: 'post',
    params
  })
}

// 更新任务
export function updateTask(params) {
  return request({
    url: '/task',
    method: 'put',
    params
  })
}

export function remove(params) {
  return request({
    url: '/task',
    method: 'delete',
    params: {
      taskClassName: params.taskClassName,
      taskGroupId: params.taskGroupId
    }
  })
}

export function logList(params) {
  return request({
    url: '/task/logList',
    method: 'get',
    params
  })
}

export function resumeTask(params) {
  return request({
    url: '/task/resume',
    method: 'POST',
    params: {
      taskClassName: params.taskClassName,
      taskGroupId: params.taskGroupId
    }
  })
}

export function pauseTask(params) {
  return request({
    url: '/task/pause',
    method: 'POST',
    params: {
      taskClassName: params.taskClassName,
      taskGroupId: params.taskGroupId
    }
  })
}

// 新增任务分组
export function addTaskGroup(params) {
  return request({
    url: '/task-group',
    method: 'post',
    params
  })
}

// 更新任务分组
export function updateTaskGroup(params) {
  return request({
    url: '/task-group',
    method: 'put',
    params
  })
}

export function taskGroupList(params) {
  return request({
    url: '/task-group/pagelist',
    method: 'get',
    params
  })
}

export function getTriggersHistory(params) {
  return request({
    url: '/qrtz-triggers-history/pagelist',
    method: 'get',
    params
  })
}

