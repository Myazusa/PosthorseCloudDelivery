package com.github.myazusa.posthorseclouddelivery.service.micro;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.f4b6a3.uuid.UuidCreator;
import com.github.myazusa.posthorseclouddelivery.core.enums.FileSortByEnum;
import com.github.myazusa.posthorseclouddelivery.core.enums.FileTypeEnum;
import com.github.myazusa.posthorseclouddelivery.core.enums.SortOrderEnum;
import com.github.myazusa.posthorseclouddelivery.core.exception.FileOperationException;
import com.github.myazusa.posthorseclouddelivery.mapper.AdMapper;
import com.github.myazusa.posthorseclouddelivery.mapper.UserAdMapper;
import com.github.myazusa.posthorseclouddelivery.model.dao.AdDAO;
import com.github.myazusa.posthorseclouddelivery.model.dao.UserAdDAO;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserRepositoryService {
    public static final String REPOSITORY_PATH = "./uploads/";
    private final AdMapper adMapper;
    private final UserAdMapper userAdMapper;

    @Autowired
    public UserRepositoryService(AdMapper adMapper, UserAdMapper userAdMapper) {
        this.adMapper = adMapper;
        this.userAdMapper = userAdMapper;
    }


    public void createRepository(UUID uuid){
        var dir = Paths.get(REPOSITORY_PATH + uuid).toAbsolutePath().normalize();

        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new FileOperationException("创建仓库失败，可能是系统没有该路径访问权限");
            }
        }
    }

    public void createFolder(UUID uuid, String folderName){
        var dir = Paths.get(REPOSITORY_PATH + uuid + "/" +folderName).toAbsolutePath().normalize();
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                throw new FileOperationException("在仓库下创建"+ folderName +"文件夹失败，可能是系统没有该路径访问权限");
            }
        }
    }

    @Transactional
    public void saveFiles(UUID uuid, FileTypeEnum fileTypeEnum, List<MultipartFile> files){
        var dir = Paths.get(REPOSITORY_PATH + uuid + "/" + fileTypeEnum.getFileTypeString());

        // 确保列表不为空
        if (files.isEmpty()) {
            throw new FileOperationException("上传的文件列表里没有文件");
        }

        // 确保目录存在
        if (!Files.exists(dir)) {
            throw new FileOperationException("该用户不存在可用的仓库");
        }

        // 循环存储文件
        for (MultipartFile file : files) {
            // 清理文件名，防止路径穿越攻击
            var fileName = StringUtils.cleanPath(Optional.ofNullable(file.getOriginalFilename()).isPresent() ? file.getOriginalFilename() : "");
            if (fileName.contains("..")) {
                throw new FileOperationException("非法的文件名：" + file.getName());
            }

            // 使用 transferTo 简化保存
            var targetFile = dir.resolve(fileName).toFile();
            // 先存文件后写数据库
            try {
                file.transferTo(targetFile);
            } catch (IOException e) {
                throw new FileOperationException("保存" + fileName + "文件失败，原因为文件写入本地时出错");
            }

            try {
                var timeOrderedEpoch = UuidCreator.getTimeOrderedEpoch();
                // 写入广告数据元表
                adMapper.insert(new AdDAO()
                        .setUuid(timeOrderedEpoch)
                        .setFilePath(REPOSITORY_PATH + uuid + "/" + fileTypeEnum.getFileTypeString() + "/" + fileName)
                        .setTitle(fileName)
                        .setType(fileTypeEnum.getFileTypeString())
                        .setFileSize(file.getSize())
                );
                // 写入连接表
                userAdMapper.insert(new UserAdDAO().setUserUuid(timeOrderedEpoch).setUserUuid(uuid));
            } catch (Exception e) {
                var tempPath = Paths.get(REPOSITORY_PATH + uuid + "/" + fileTypeEnum.getFileTypeString() + "/" + fileName);
                if (Files.exists(tempPath)) {
                    try {
                        Files.delete(tempPath);
                    } catch (IOException ex) {
                        throw new FileOperationException("删除" + fileName + "文件失败，写入数据库失败后清除文件遇到问题");
                    }
                }
                throw new FileOperationException("保存" + fileName + "文件失败，原因是写入数据库失败");
            }
        }
    }

    public List<AdDAO> listFiles(UUID uuid, FileTypeEnum fileType, Integer pageNumber, Integer pageSize, FileSortByEnum fileSortByEnum, SortOrderEnum sortOrderEnum, String keyword){
        Page<AdDAO> page = new Page<>(pageNumber, pageSize);
        var wrapper = new MPJLambdaWrapper<UserAdDAO>();

        // 筛选属于该用户的
        wrapper.selectAll(UserAdDAO.class).eq(UserAdDAO::getUserUuid, uuid);

        // 连接表
        wrapper.leftJoin(AdDAO.class, AdDAO::getUuid, UserAdDAO::getAdUuid);

        // 筛选属于该分类的
        wrapper.eq(AdDAO::getType,fileType.getFileTypeString());

        // 如果有关键词，先搜索关键词
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(AdDAO::getTitle, keyword));
        }

        // 排序
        if (sortOrderEnum != null) {
            boolean isAsc = "asc".equalsIgnoreCase(sortOrderEnum.getSortOrderString());
            wrapper.orderBy(true, isAsc, getSortField(fileSortByEnum));
        }else {
            wrapper.orderByDesc(AdDAO::getCreatedAt);
        }

        // 分页
        Page<AdDAO> adDAOPage = userAdMapper.selectJoinPage(
                page,
                AdDAO.class,wrapper);

        return new ArrayList<>(adDAOPage.getRecords());
    }

    private SFunction<AdDAO, ?> getSortField(FileSortByEnum sortBy) {
        return switch (sortBy) {
            case uuid -> AdDAO::getUuid;
            case title -> AdDAO::getTitle;
            case createdAt -> AdDAO::getCreatedAt;
        };
    }
}
