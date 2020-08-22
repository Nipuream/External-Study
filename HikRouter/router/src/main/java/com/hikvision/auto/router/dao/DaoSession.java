package com.hikvision.auto.router.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.hikvision.auto.router.info.Position;
import com.hikvision.auto.router.info.Contract;
import com.hikvision.auto.router.info.DriverInfo;

import com.hikvision.auto.router.dao.PositionDao;
import com.hikvision.auto.router.dao.ContractDao;
import com.hikvision.auto.router.dao.DriverInfoDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig positionDaoConfig;
    private final DaoConfig contractDaoConfig;
    private final DaoConfig driverInfoDaoConfig;

    private final PositionDao positionDao;
    private final ContractDao contractDao;
    private final DriverInfoDao driverInfoDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        positionDaoConfig = daoConfigMap.get(PositionDao.class).clone();
        positionDaoConfig.initIdentityScope(type);

        contractDaoConfig = daoConfigMap.get(ContractDao.class).clone();
        contractDaoConfig.initIdentityScope(type);

        driverInfoDaoConfig = daoConfigMap.get(DriverInfoDao.class).clone();
        driverInfoDaoConfig.initIdentityScope(type);

        positionDao = new PositionDao(positionDaoConfig, this);
        contractDao = new ContractDao(contractDaoConfig, this);
        driverInfoDao = new DriverInfoDao(driverInfoDaoConfig, this);

        registerDao(Position.class, positionDao);
        registerDao(Contract.class, contractDao);
        registerDao(DriverInfo.class, driverInfoDao);
    }
    
    public void clear() {
        positionDaoConfig.getIdentityScope().clear();
        contractDaoConfig.getIdentityScope().clear();
        driverInfoDaoConfig.getIdentityScope().clear();
    }

    public PositionDao getPositionDao() {
        return positionDao;
    }

    public ContractDao getContractDao() {
        return contractDao;
    }

    public DriverInfoDao getDriverInfoDao() {
        return driverInfoDao;
    }

}
