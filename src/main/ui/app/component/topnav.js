import Link from "next/link";
import { HomeIcon, UsersIcon } from '@heroicons/react/24/outline';
import styles from './css/topnav.module.css'

export default function TopNav() {
  return (
    <div className={styles.navOuter}>
      <Link className={`${styles.navItem} ${styles.left}`} href="/"> 
        <HomeIcon className={styles.Icon} />
      </Link>
      <Link className={styles.navItem} href="/users">
        <UsersIcon className={styles.icon} />
      </Link>
    </div>
  );
};
