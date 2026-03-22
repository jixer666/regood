import request from '@/utils/request'

export function uploadFile(data) {
    return request({
        url: '/system/oss/upload',
        method: 'post',
        data: data,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

export function getFilePage(data) {
    return request({
        url: '/system/file/page',
        method: 'get',
        params: data
    })
}

export function updateFile(data) {
    return request({
        url: '/system/file',
        method: 'put',
        data: data
    })
}

export function addFile(data) {
    return request({
        url: '/system/file',
        method: 'post',
        data: data
    })
}


export function deleteFile(data) {
    return request({
        url: '/system/file',
        method: 'delete',
        data: data
    })
}