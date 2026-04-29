import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { User, FileText, Heart, Camera, Save, AlertCircle } from 'lucide-react';
import { getCurrentUser, userApi, logout, User as UserType } from '../../services/api';
import './ProfilePage.css';

export const ProfilePage = () => {
  const navigate = useNavigate();
  const currentUser = getCurrentUser();
  const [user, setUser] = useState<UserType | null>(currentUser);
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    nickname: currentUser?.nickname || '',
    bio: currentUser?.bio || '',
    avatar: currentUser?.avatar || '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!currentUser) {
      navigate('/login');
    }
  }, [currentUser, navigate]);

  const handleSave = async () => {
    if (!user) return;
    setLoading(true);
    setError('');

    try {
      const updated = await userApi.update(user.id, {
        nickname: formData.nickname,
        bio: formData.bio,
        avatar: formData.avatar,
      });
      localStorage.setItem('user', JSON.stringify(updated));
      setUser(updated);
      setIsEditing(false);
    } catch (err: any) {
      setError(err.message || '保存失败');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/');
    window.location.reload();
  };

  const handleCancel = () => {
    setFormData({
      nickname: user?.nickname || '',
      bio: user?.bio || '',
      avatar: user?.avatar || '',
    });
    setIsEditing(false);
  };

  if (!user) {
    return <div className="profile-loading">加载中...</div>;
  }

  return (
    <div className="profile-page">
      <div className="profile-container">
        <div className="profile-header">
          <div className="profile-avatar-section">
            {isEditing ? (
              <div className="avatar-edit">
                <img src={formData.avatar} alt="头像" className="profile-avatar" />
                <label className="avatar-upload-btn">
                  <Camera size={20} />
                  <input
                    type="text"
                    value={formData.avatar}
                    onChange={(e) => setFormData({ ...formData, avatar: e.target.value })}
                    placeholder="输入头像 URL"
                    className="avatar-input"
                  />
                </label>
              </div>
            ) : (
              <img src={user.avatar} alt={user.nickname} className="profile-avatar" />
            )}
          </div>

          <div className="profile-info">
            <h1>{user.nickname}</h1>
            <p className="profile-username">@{user.username}</p>
            {user.bio && <p className="profile-bio">{user.bio}</p>}
          </div>

          <div className="profile-actions">
            {isEditing ? (
              <>
                <button className="btn btn-primary" onClick={handleSave} disabled={loading}>
                  <Save size={18} />
                  保存
                </button>
                <button className="btn btn-secondary" onClick={handleCancel}>
                  取消
                </button>
              </>
            ) : (
              <button className="btn btn-secondary" onClick={() => setIsEditing(true)}>
                编辑资料
              </button>
            )}
          </div>
        </div>

        {error && (
          <div className="profile-error">
            <AlertCircle size={18} />
            <span>{error}</span>
          </div>
        )}

        {isEditing && (
          <div className="profile-edit-form">
            <div className="form-group">
              <label>
                <User size={18} />
                昵称
              </label>
              <input
                type="text"
                value={formData.nickname}
                onChange={(e) => setFormData({ ...formData, nickname: e.target.value })}
                placeholder="请输入昵称"
              />
            </div>

            <div className="form-group">
              <label>
                <FileText size={18} />
                个人简介
              </label>
              <textarea
                value={formData.bio}
                onChange={(e) => setFormData({ ...formData, bio: e.target.value })}
                placeholder="介绍一下自己..."
                rows={3}
              />
            </div>

            <div className="form-group">
              <label>
                <Camera size={18} />
                头像 URL
              </label>
              <input
                type="text"
                value={formData.avatar}
                onChange={(e) => setFormData({ ...formData, avatar: e.target.value })}
                placeholder="输入头像图片地址"
              />
            </div>
          </div>
        )}

        <div className="profile-stats">
          <div className="stat-item">
            <span className="stat-value">0</span>
            <span className="stat-label">我的食谱</span>
          </div>
          <div className="stat-item">
            <span className="stat-value">0</span>
            <span className="stat-label">关注</span>
          </div>
          <div className="stat-item">
            <span className="stat-value">0</span>
            <span className="stat-label">粉丝</span>
          </div>
        </div>

        <div className="profile-menu">
          <button className="profile-menu-item" onClick={() => navigate('/favorites')}>
            <Heart size={20} />
            <span>我的收藏</span>
          </button>
        </div>

        <div className="profile-danger-zone">
          <h3>危险区域</h3>
          <button className="btn btn-danger" onClick={handleLogout}>
            退出登录
          </button>
        </div>
      </div>
    </div>
  );
};
