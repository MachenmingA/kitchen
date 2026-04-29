import { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Search, Menu, X, ChefHat, LogOut, Heart, Settings, Plus } from 'lucide-react';
import { User as UserType } from '../../services/api';
import styles from './Header.module.css';
import './Header.css';

interface HeaderProps {
  onSearch: (query: string) => void;
  user?: UserType | null;
  onLogout?: () => void;
}

export function Header({ onSearch, user, onLogout }: HeaderProps) {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const [searchValue, setSearchValue] = useState('');
  const [isUserMenuOpen, setIsUserMenuOpen] = useState(false);
  const location = useLocation();

  const navLinks = [
    { path: '/', label: '首页' },
    { path: '/categories', label: '分类' },
    { path: '/favorites', label: '收藏' },
  ];

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setSearchValue(value);
    onSearch(value);
  };

  const clearSearch = () => {
    setSearchValue('');
    onSearch('');
  };

  const handleLogout = () => {
    onLogout?.();
    setIsUserMenuOpen(false);
  };

  return (
    <header className={styles.header}>
      <div className={styles.container}>
        <Link to="/" className={styles.logo}>
          <ChefHat className={styles.logoIcon} />
          <span className={styles.logoText}>我的厨房</span>
        </Link>

        <nav className={`${styles.nav} ${isMenuOpen ? styles.navOpen : ''}`}>
          {navLinks.map((link) => (
            <Link
              key={link.path}
              to={link.path}
              className={`${styles.navLink} ${
                location.pathname === link.path ? styles.navLinkActive : ''
              }`}
              onClick={() => setIsMenuOpen(false)}
            >
              {link.label}
            </Link>
          ))}
        </nav>

        <div className={styles.searchWrapper}>
          <Search className={styles.searchIcon} size={18} />
          <input
            type="text"
            placeholder="搜索食谱..."
            value={searchValue}
            onChange={handleSearchChange}
            className={styles.searchInput}
          />
          {searchValue && (
            <button onClick={clearSearch} className={styles.clearButton}>
              <X size={16} />
            </button>
          )}
        </div>

        {user ? (
          <div className={styles.userSection}>
            <Link to="/create-recipe" className={styles.createButton}>
              <Plus size={18} />
              <span>创建食谱</span>
            </Link>
            <div className={styles.userMenu}>
              <button
                className={styles.userButton}
                onClick={() => setIsUserMenuOpen(!isUserMenuOpen)}
              >
                <img src={user.avatar} alt={user.nickname} className={styles.avatar} />
              </button>
              {isUserMenuOpen && (
                <div className={styles.dropdownMenu}>
                  <div className={styles.userInfo}>
                    <img src={user.avatar} alt={user.nickname} className={styles.dropdownAvatar} />
                    <div>
                      <p className={styles.nickname}>{user.nickname}</p>
                      <p className={styles.username}>@{user.username}</p>
                    </div>
                  </div>
                  <div className={styles.menuDivider} />
                  <Link to="/favorites" className={styles.menuItem} onClick={() => setIsUserMenuOpen(false)}>
                    <Heart size={16} />
                    <span>我的收藏</span>
                  </Link>
                  <Link to="/profile" className={styles.menuItem} onClick={() => setIsUserMenuOpen(false)}>
                    <Settings size={16} />
                    <span>账号设置</span>
                  </Link>
                  <div className={styles.menuDivider} />
                  <button className={styles.menuItem} onClick={handleLogout}>
                    <LogOut size={16} />
                    <span>退出登录</span>
                  </button>
                </div>
              )}
            </div>
          </div>
        ) : (
          <div className={styles.authButtons}>
            <Link to="/login" className={styles.loginButton}>登录</Link>
            <Link to="/register" className={styles.registerButton}>注册</Link>
          </div>
        )}

        <button
          className={styles.menuButton}
          onClick={() => setIsMenuOpen(!isMenuOpen)}
          aria-label="菜单"
        >
          {isMenuOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </div>
    </header>
  );
}
