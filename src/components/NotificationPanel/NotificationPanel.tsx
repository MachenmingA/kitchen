import { useState, useEffect } from 'react';
import { Bell, Heart, MessageCircle, UserPlus, Check, X } from 'lucide-react';
import { notificationApi, getCurrentUser, Notification } from '../../services/api';
import './NotificationPanel.css';

interface NotificationPanelProps {
  onClose: () => void;
}

export function NotificationPanel({ onClose }: NotificationPanelProps) {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(true);

  const currentUser = getCurrentUser();

  useEffect(() => {
    loadNotifications();
  }, []);

  const loadNotifications = async () => {
    if (!currentUser) return;

    setLoading(true);
    try {
      const [list, count] = await Promise.all([
        notificationApi.getList(currentUser.id, 20),
        notificationApi.getUnreadCount(currentUser.id),
      ]);
      setNotifications(list);
      setUnreadCount(count);
    } catch (error) {
      console.error('Failed to load notifications:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleMarkAsRead = async (id: number) => {
    try {
      await notificationApi.markAsRead(id);
      setNotifications(notifications.map(n =>
        n.id === id ? { ...n, isRead: 1 } : n
      ));
      setUnreadCount(Math.max(0, unreadCount - 1));
    } catch (error) {
      console.error('Failed to mark as read:', error);
    }
  };

  const handleMarkAllAsRead = async () => {
    if (!currentUser) return;
    try {
      await notificationApi.markAllAsRead(currentUser.id);
      setNotifications(notifications.map(n => ({ ...n, isRead: 1 })));
      setUnreadCount(0);
    } catch (error) {
      console.error('Failed to mark all as read:', error);
    }
  };

  const getIcon = (type: string) => {
    switch (type) {
      case 'favorite':
        return <Heart size={18} />;
      case 'comment':
        return <MessageCircle size={18} />;
      case 'follow':
        return <UserPlus size={18} />;
      default:
        return <Bell size={18} />;
    }
  };

  const getIconClass = (type: string) => {
    switch (type) {
      case 'favorite':
        return 'icon-favorite';
      case 'comment':
        return 'icon-comment';
      case 'follow':
        return 'icon-follow';
      default:
        return '';
    }
  };

  return (
    <div className="notification-panel">
      <div className="notification-header">
        <h3>
          <Bell size={18} />
          通知
        </h3>
        <div className="header-actions">
          {unreadCount > 0 && (
            <button onClick={handleMarkAllAsRead} className="mark-all-btn">
              <Check size={14} />
              全部已读
            </button>
          )}
          <button onClick={onClose} className="close-btn">
            <X size={18} />
          </button>
        </div>
      </div>

      <div className="notification-list">
        {loading ? (
          <div className="notification-loading">加载中...</div>
        ) : notifications.length > 0 ? (
          notifications.map((notification) => (
            <div
              key={notification.id}
              className={`notification-item ${notification.isRead === 0 ? 'unread' : ''}`}
              onClick={() => notification.isRead === 0 && handleMarkAsRead(notification.id)}
            >
              <div className={`notification-icon ${getIconClass(notification.type)}`}>
                {getIcon(notification.type)}
              </div>
              <div className="notification-content">
                <p className="notification-title">{notification.title}</p>
                <p className="notification-text">{notification.content}</p>
                <span className="notification-time">
                  {formatTime(notification.createTime)}
                </span>
              </div>
              {notification.isRead === 0 && <div className="unread-dot" />}
            </div>
          ))
        ) : (
          <div className="notification-empty">
            <Bell size={40} />
            <p>暂无通知</p>
          </div>
        )}
      </div>
    </div>
  );
}

function formatTime(timeString: string): string {
  const date = new Date(timeString);
  const now = new Date();
  const diff = now.getTime() - date.getTime();

  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(diff / 3600000);
  const days = Math.floor(diff / 86400000);

  if (minutes < 1) return '刚刚';
  if (minutes < 60) return `${minutes}分钟前`;
  if (hours < 24) return `${hours}小时前`;
  if (days < 7) return `${days}天前`;
  return date.toLocaleDateString();
}
