package com.github.myazusa.posthorseclouddelivery.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.myazusa.posthorseclouddelivery.core.enums.FileSortByEnum;
import com.github.myazusa.posthorseclouddelivery.core.enums.FileTypeEnum;
import com.github.myazusa.posthorseclouddelivery.core.enums.SortOrderEnum;
import com.github.myazusa.posthorseclouddelivery.core.exception.InvalidParamException;
import com.github.myazusa.posthorseclouddelivery.model.dto.*;
import com.github.myazusa.posthorseclouddelivery.service.micro.UserRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserRepositoryCompoService {
    private final UserRepositoryService userRepositoryService;
    public final static String VIDEO_FOLDER_NAME = "video";
    public final static String IMAGE_FOLDER_NAME = "image";

    @Autowired
    public UserRepositoryCompoService(UserRepositoryService userRepositoryService) {
        this.userRepositoryService = userRepositoryService;
    }

    /**
     * 创建用户仓库的方法，一般是初始化用户的仓库，应该在用户注册后立马调用
     * @param authentication
     */
    public void createRepository(Authentication authentication){
        var user = (UserDetailsDTO)authentication.getPrincipal();

        // 在本地创建用户专属目录
        userRepositoryService.createRepository(user.getUuid());
        userRepositoryService.createFolder(user.getUuid(), VIDEO_FOLDER_NAME);
        userRepositoryService.createFolder(user.getUuid(), IMAGE_FOLDER_NAME);
    }

    /**
     * 保存文件的方法，批量
     * @param authentication
     * @param uploadFilesRequestDTO
     */
    public void saveFiles(Authentication authentication, UploadFilesRequestDTO uploadFilesRequestDTO){
        var user = (UserDetailsDTO)authentication.getPrincipal();
        FileTypeEnum fileTypeEnum;
        try {
            fileTypeEnum = FileTypeEnum.fromString(uploadFilesRequestDTO.getFileType());
        }catch (IllegalArgumentException e){
            throw new InvalidParamException("传入的fileType参数不正确，不允许上传video和image之外的类型");
        }

        userRepositoryService.saveFiles(user.getUuid(),fileTypeEnum,uploadFilesRequestDTO.getFiles());
    }

    /**
     * 搜索文件或列出文件的方法
     * @param authentication
     * @param listFilesRequestDTO 支持传入分页和关键词，也可以不传
     * @return
     */
    public List<FilesResponseDTO> listFiles(Authentication authentication, ListFilesRequestDTO listFilesRequestDTO){
        var user = (UserDetailsDTO)authentication.getPrincipal();

        FileTypeEnum fileTypeEnum;
        FileSortByEnum fileSortByEnum;
        SortOrderEnum sortOrderEnum;
        // 文件类型和排序参数校验
        try {
            fileTypeEnum = FileTypeEnum.fromString(listFilesRequestDTO.getFileType());
        }catch (IllegalArgumentException e){
            throw new InvalidParamException("传入的fileType参数不正确，不允许上传video和image之外的类型");
        }
        try {
            fileSortByEnum = FileSortByEnum.fromString(listFilesRequestDTO.getSortBy());
        }catch (IllegalArgumentException e){
            throw new InvalidParamException("传入的sortBy参数不正确，不允许数据库没有的字段");
        }
        try {
            sortOrderEnum = SortOrderEnum.fromString(listFilesRequestDTO.getSortOrder());
        }catch (IllegalArgumentException e){
            throw new InvalidParamException("传入的sortOrder参数不正确，不允许上传asc和desc之外的类型");
        }

        // 分页参数正确性校验
        if (listFilesRequestDTO.getPageNumber() == null || listFilesRequestDTO.getPageSize() == null){
            throw new InvalidParamException("分页查询的分页参数不可为空");
        }
        if (listFilesRequestDTO.getPageNumber() < 1) {
            throw new InvalidParamException("分页查询页码不允许小于1");
        }
        if (listFilesRequestDTO.getPageSize() < 1 || listFilesRequestDTO.getPageSize() > 100) {
            throw new InvalidParamException("分页查询每页大小不允许小于1或大于100");
        }

        // 拆包传入
        var adDAOList = userRepositoryService.listFiles(user.getUuid(),
                fileTypeEnum,
                listFilesRequestDTO.getPageNumber(),
                listFilesRequestDTO.getPageSize(),
                fileSortByEnum,
                sortOrderEnum,
                listFilesRequestDTO.getKeyword());

        // 遍历数据库传回的对象，封装为前端用的数据包
        List<FilesResponseDTO> filesResponseDTOList = new ArrayList<>();
        adDAOList.forEach(adDAO -> {
            var filesResponseDTO = new FilesResponseDTO();
            filesResponseDTO.setFileName(adDAO.getTitle())
                    .setFileSize(adDAO.getFileSize())
                    .setDuration(adDAO.getDuration())
                    .setUuid(adDAO.getUuid())
                    .setPriority(adDAO.getPriority())
                    .setCreatedAt(adDAO.getCreatedAt())
                    .setFiletype(adDAO.getType());
            filesResponseDTOList.add(filesResponseDTO);
        });

        return filesResponseDTOList;
    }

    /**
     * 删除广告数据元，批量
     * @param authentication
     * @param deleteFilesRequestDTO 对象里要带有广告数据源uuid的列表
     */
    public void deleteFiles(Authentication authentication, DeleteFilesRequestDTO deleteFilesRequestDTO){
        var user = (UserDetailsDTO)authentication.getPrincipal();
        List<UUID> fileUuidList;
        try {
            fileUuidList = deleteFilesRequestDTO.getFileUuidList().stream().map(UuidCreator::fromString).toList();
        }catch (Exception e) {
            throw new InvalidParamException("传入的参数列表中包含无效的广告数据元uuid");
        }
        userRepositoryService.deleteFiles(user.getUuid(), FileTypeEnum.fromString(deleteFilesRequestDTO.getFileType()),fileUuidList);
    }
}
