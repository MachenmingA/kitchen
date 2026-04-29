import { useState, useEffect } from 'react';
import { UserPlus, UserCheck } from 'lucide-react';
import { followApi, getCurrentUser } from '../../services/api';
import './FollowButton.css';

interface FollowButtonProps {
  userId: number;
  size?: 'small' | 'medium' | 'large';
}

export function FollowButton({ userId, size = 'medium' }: FollowButtonProps) {
  const [isFollowing, setIsFollowing] = useState(false);
  const [loading, setLoading] = useState(true);

  const currentUser = getCurrentUser();

  useEffect(() => {
    checkFollowStatus();
  }, [userId]);

  const checkFollowStatus = async () => {
    if (!currentUser || currentUser.id === userId) {
      setLoading(false);
      return;
    }

    try {
      const following = await followApi.isFollowing(currentUser.id, userId);
      setIsFollowing(following);
    } catch (error) {
      console.error('Failed to check follow status:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleClick = async () => {
    if (!currentUser) {
      alert('请先登录');
      return;
    }

    if (currentUser.id === userId) return;

    setLoading(true);
    try {
      if (isFollowing) {
        await followApi.unfollow(currentUser.id, userId);
      } else {
        await followApi.follow(currentUser.id, userId);
      }
      setIsFollowing(!isFollowing);
    } catch (error) {
      console.error('Failed to toggle follow:', error);
    } finally {
      setLoading(false);
    }
  };

  if (!currentUser || currentUser.id === userId) {
    return null;
  }

  const sizeClass = `follow-button-${size}`;

  return (
    <button
      className={`follow-button ${sizeClass} ${isFollowing ? 'following' : ''}`}
      onClick={handleClick}
      disabled={loading}
    >
      {isFollowing ? (
        <>
          <UserCheck size={16} />
          <span>已关注</span>
        </>
      ) : (
        <>
          <UserPlus size={16} />
          <span>关注</span>
        </>
      )}
    </button>
  );
}
