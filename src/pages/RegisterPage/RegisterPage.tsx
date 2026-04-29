import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { ChefHat, Mail, Lock, User, AlertCircle } from 'lucide-react';
import { authApi } from '../../services/api';
import './AuthPage.css';

export const RegisterPage = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    confirmPassword: '',
    nickname: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (formData.password !== formData.confirmPassword) {
      setError('两次输入的密码不一致');
      return;
    }

    if (formData.password.length < 6) {
      setError('密码长度不能少于6位');
      return;
    }

    setLoading(true);

    try {
      await authApi.register(formData.username, formData.password, formData.nickname);
      navigate('/login');
    } catch (err: any) {
      setError(err.message || '注册失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-container">
        <div className="auth-header">
          <ChefHat size={48} className="auth-logo" />
          <h1>创建账号</h1>
          <p>加入我的厨房</p>
        </div>

        {error && (
          <div className="auth-error">
            <AlertCircle size={18} />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label htmlFor="username">
              <User size={18} />
              <span>用户名</span>
            </label>
            <input
              type="text"
              id="username"
              value={formData.username}
              onChange={(e) => setFormData({ ...formData, username: e.target.value })}
              placeholder="请输入用户名"
              required
              minLength={3}
            />
          </div>

          <div className="form-group">
            <label htmlFor="nickname">
              <Mail size={18} />
              <span>昵称</span>
            </label>
            <input
              type="text"
              id="nickname"
              value={formData.nickname}
              onChange={(e) => setFormData({ ...formData, nickname: e.target.value })}
              placeholder="请输入昵称（选填）"
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">
              <Lock size={18} />
              <span>密码</span>
            </label>
            <input
              type="password"
              id="password"
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              placeholder="请输入密码（至少6位）"
              required
              minLength={6}
            />
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">
              <Lock size={18} />
              <span>确认密码</span>
            </label>
            <input
              type="password"
              id="confirmPassword"
              value={formData.confirmPassword}
              onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
              placeholder="请再次输入密码"
              required
            />
          </div>

          <button type="submit" className="auth-button" disabled={loading}>
            {loading ? '注册中...' : '注册'}
          </button>
        </form>

        <div className="auth-footer">
          <p>
            已有账号？<Link to="/login">立即登录</Link>
          </p>
        </div>
      </div>
    </div>
  );
};
